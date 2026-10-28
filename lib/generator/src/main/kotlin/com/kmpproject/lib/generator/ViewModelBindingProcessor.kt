package com.kmpproject.lib.generator

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.kmpproject.lib.annotation.ScopedViewModel
import com.kmpproject.lib.annotation.ViewModel
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

internal class ViewModelBindingProcessor(
  private val env: SymbolProcessorEnvironment,
) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val viewModels = resolver
      .getSymbolsWithAnnotation(ViewModel::class.asClassName().reflectionName())
      .toList()
    generateBinding(
      resolver = resolver,
      declarations = viewModels
    )
    return emptyList()
  }

  @Suppress("SpreadOperator")
  @OptIn(KspExperimental::class)
  fun generateBinding(
    resolver: Resolver,
    declarations: List<KSAnnotated>,
  ) {
    declarations
      .filter { declaration -> declaration.isAnnotationPresent(ViewModel::class) }
      .groupBy { declaration ->
        val viewModelAnnotation = ViewModel::class
          .qualifiedName
          ?.let(resolver::getKSNameFromString)
          ?.let(resolver::getClassDeclarationByName)
        (
          declaration.annotations
            .toList()
            .first { it.annotationType.resolve() == viewModelAnnotation?.asStarProjectedType() }
            .arguments
            .first()
            .value as? KSType
        )?.toClassName()
          ?: error("no class name for $declaration")
      }.entries
      .map { (scope, declarations) ->
        val packageName = scope.packageName
        // when multiple modules contribute to the same scope, it is necessary to avoid naming conflicts
        val moduleName = "${scope.simpleName.substringBeforeLast("Scope")}UiModule${randomString()}"
        buildUiModule(
          resolver = resolver,
          packageName = packageName,
          moduleName = moduleName,
          scope = scope,
          declarations = declarations,
        ).writeTo(
          codeGenerator = env.codeGenerator,
          dependencies = Dependencies(false, *declarations.mapNotNull { it.containingFile }.toTypedArray())
        )
      }
  }

  @OptIn(KspExperimental::class)
  @Suppress("SpreadOperator")
  private fun buildUiModule(
    resolver: Resolver,
    packageName: String,
    moduleName: String,
    scope: ClassName,
    declarations: List<KSAnnotated>,
  ): FileSpec {
    val viewModelProvider = resolver
      .getClassDeclarationByName(
        name = resolver.getKSNameFromString("com.kmpproject.core.ui.viewmodel.ViewModelProvider")
      )?.asStarProjectedType()
      ?: error("ViewModelProvider not found, possibly due to class moved to another package. Check generator")

    val assistedViewModelProvider = resolver
      .getClassDeclarationByName(
        name = resolver.getKSNameFromString("com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider")
      )?.asStarProjectedType()
      ?: error("AssistedViewModelProvider not found, possibly due to class moved to another package. Check generator")

    return FileSpec
      .builder(packageName, moduleName)
      .apply {
        val scopeArgument = CodeBlock.builder().add(CodeBlock.of("%T::class", scope)).build()

        val viewModels = declarations
          .filter { it.isAnnotationPresent(ViewModel::class) }
          .filterIsInstance<KSClassDeclaration>()

        val assistedFactories = generateAssistedFactories(viewModels)

        val (factoryProviders, providers) = viewModels
          .map { it.asStarProjectedType().toClassName() }
          .partition { it in assistedFactories }

        addType(
          TypeSpec
            .interfaceBuilder(moduleName)
            .addAnnotations(
              listOf(
                AnnotationSpec.builder(ContributesTo::class).addMember(scopeArgument).build(),
              )
            ).addFunctions(
              viewModels
                .map { declaration ->
                  val vmClassName = declaration.asStarProjectedType().toClassName()
                  val name = vmClassName
                    .enclosingClassName()
                    ?.let { "${it.simpleName}${vmClassName.simpleName}" }
                    ?: vmClassName.simpleName
                  FunSpec
                    .builder("bind$name")
                    .addAnnotations(
                      listOf(
                        AnnotationSpec.builder(Provides::class).build(),
                        AnnotationSpec.builder(IntoSet::class).build(),
                      )
                    ).addParameter(
                      if (vmClassName in assistedFactories) {
                        ParameterSpec
                          .builder(
                            name = "factory",
                            type = LambdaTypeName.get(
                              parameters = assistedFactories[vmClassName]
                                ?.properties
                                .orEmpty()
                                .map { property ->
                                  ParameterSpec
                                    .builder(
                                      name = property.name?.getShortName().orEmpty(),
                                      type = property.type.toTypeName(),
                                    ).build()
                                },
                              returnType = vmClassName,
                            )
                          ).build()
                      } else {
                        ParameterSpec
                          .builder(
                            name = "factory",
                            type = LambdaTypeName.get(
                              parameters = emptyList(),
                              returnType = vmClassName,
                            )
                          ).build()
                      }
                    ).addCode(
                      if (vmClassName in assistedFactories) {
                        val arguments =
                          assistedFactories[vmClassName]?.properties.orEmpty().map { it.type.toTypeName() }
                        val template = List(arguments.size) { index -> "args[$index] as %T" }.joinToString(", ")
                        val key = "${vmClassName.simpleName}::class.qualifiedName.orEmpty()"
                        val factory = "{ args -> factory($template) }"
                        CodeBlock
                          .builder()
                          .addStatement(
                            "return %T(\n  key = $key,\n  factory = $factory\n)",
                            *(listOf(assistedViewModelProvider.toTypeName()) + arguments).toTypedArray()
                          ).build()
                      } else {
                        val key = "${vmClassName.simpleName}::class.qualifiedName.orEmpty()"
                        CodeBlock
                          .builder()
                          .addStatement(
                            "return %T(\n  key = $key,\n  factory = factory\n)",
                            viewModelProvider.toTypeName()
                          ).build()
                      }
                    ).returns(
                      returnType = if (vmClassName in assistedFactories) {
                        assistedViewModelProvider.toClassName()
                      } else {
                        viewModelProvider.toClassName()
                      }.copy(
                        annotations = listOf(
                          AnnotationSpec.builder(ScopedViewModel::class).addMember(scopeArgument).build()
                        )
                      )
                    ).build()
                }.plus(
                  listOfNotNull(
                    // when multiple modules contribute to the same scope, it is necessary to avoid naming conflicts
                    FunSpec
                      .builder("emptyViewModelProviders${randomString()}")
                      .addAnnotations(
                        listOf(
                          AnnotationSpec.builder(Provides::class).build(),
                          AnnotationSpec.builder(IntoSet::class).build(),
                        )
                      ).addStatement(
                        "return ViewModelProvider(\"empty\") { error(\"should not be instantiated\") }"
                      ).returns(
                        returnType = viewModelProvider
                          .toClassName()
                          .copy(
                            annotations = listOf(
                              AnnotationSpec.builder(ScopedViewModel::class).addMember(scopeArgument).build()
                            )
                          )
                      ).build()
                      .takeIf { providers.isEmpty() },
                    // when multiple modules contribute to the same scope, it is necessary to avoid naming conflicts
                    FunSpec
                      .builder("emptyAssistedViewModelProviders${randomString()}")
                      .addAnnotations(
                        listOf(
                          AnnotationSpec.builder(Provides::class).build(),
                          AnnotationSpec.builder(IntoSet::class).build(),
                        )
                      ).addStatement(
                        "return AssistedViewModelProvider(\"empty\") { error(\"should not be instantiated\") }"
                      ).returns(
                        returnType = assistedViewModelProvider
                          .toClassName()
                          .copy(
                            annotations = listOf(
                              AnnotationSpec.builder(ScopedViewModel::class).addMember(scopeArgument).build()
                            )
                          )
                      ).build()
                      .takeIf { factoryProviders.isEmpty() },
                  )
                )
            ).build()
        )
      }.build()
  }

  @OptIn(KspExperimental::class)
  private fun generateAssistedFactories(
    viewModels: List<KSClassDeclaration>,
  ): Map<ClassName, AssistedFactory> {
    return viewModels
      .mapNotNull { declaration ->
        val assistedProperties = declaration.primaryConstructor
          ?.parameters
          .orEmpty()
          .filter { it.isAnnotationPresent(Assisted::class) }
        if (assistedProperties.isNotEmpty()) {
          declaration.toClassName() to AssistedFactory(
            declaration = declaration,
            properties = assistedProperties,
          )
        } else {
          null
        }
      }.toMap()
  }

  private data class AssistedFactory(
    val declaration: KSClassDeclaration,
    val properties: List<KSValueParameter>
  )
}

private fun randomString(): String {
  val allowedChars = ('a'..'z')
  return (1..10)
    .map { allowedChars.random() }
    .joinToString("")
}

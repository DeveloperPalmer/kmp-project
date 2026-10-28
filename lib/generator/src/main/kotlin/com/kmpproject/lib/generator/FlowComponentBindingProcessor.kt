package com.kmpproject.lib.generator

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.kmpproject.lib.annotation.FlowCoroutineScope
import com.kmpproject.lib.annotation.MergeSubcomponent
import com.kmpproject.lib.annotation.ScopedViewModel
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

internal class FlowComponentBindingProcessor(
  private val env: SymbolProcessorEnvironment,
) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val componentDeclarations = resolver
      .getSymbolsWithAnnotation(MergeSubcomponent::class.asClassName().reflectionName())
      .filterIsInstance<KSClassDeclaration>()
      .toList()
    generateCode(
      resolver = resolver,
      declarations = componentDeclarations
    )
    return emptyList()
  }

  private fun generateCode(
    resolver: Resolver,
    declarations: List<KSClassDeclaration>,
  ) {
    declarations
      .associateWith { declaration -> declaration.getScope(resolver) }
      .entries
      .map { (declaration, scope) ->
        val packageName = scope.packageName
        val componentName = "${scope.simpleName}FlowComponent"
        buildFlowComponent(
          resolver = resolver,
          packageName = packageName,
          componentName = componentName,
          scope = scope,
          componentDeclaration = declaration,
        ).writeTo(
          codeGenerator = env.codeGenerator,
          dependencies = Dependencies(
            false,
            declaration.containingFile!!
          )
        )
      }
  }

  private fun buildFlowComponent(
    resolver: Resolver,
    packageName: String,
    componentName: String,
    scope: ClassName,
    componentDeclaration: KSClassDeclaration,
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

    val viewModelProviders = Set::class
      .asClassName()
      .plusParameter(viewModelProvider.toClassName())

    val assistedViewModelProviders = Set::class
      .asClassName()
      .plusParameter(assistedViewModelProvider.toClassName())

    return FileSpec
      .builder(packageName, componentName)
      .apply {
        val scopeArgument = scope.asCodeBlock()
        val baseInterfaceName = baseChildComponentConvention(scope)

        addType(
          TypeSpec
            .interfaceBuilder(baseInterfaceName)
            .addSuperinterface(componentDeclaration.toClassName())
            .addAnnotations(
              listOf(
                AnnotationSpec.builder(ContributesTo::class).addMember(scopeArgument).build(),
              )
            ).addFunctions(
              listOf(
                FunSpec
                  .builder("viewModelProviders")
                  .addModifiers(KModifier.ABSTRACT)
                  .addModifiers(KModifier.OVERRIDE)
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(ScopedViewModel::class).addMember(scopeArgument).build(),
                    )
                  ).returns(
                    returnType = viewModelProviders
                  ).build(),
                FunSpec
                  .builder("assistedViewModelProviders")
                  .addModifiers(KModifier.ABSTRACT)
                  .addModifiers(KModifier.OVERRIDE)
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(ScopedViewModel::class).addMember(scopeArgument).build(),
                    )
                  ).returns(
                    returnType = assistedViewModelProviders
                  ).build(),
                FunSpec
                  .builder("coroutineScope")
                  .addModifiers(KModifier.OVERRIDE, KModifier.ABSTRACT)
                  .addAnnotations(
                    listOf(AnnotationSpec.builder(FlowCoroutineScope::class).addMember(scopeArgument).build())
                  ).returns(returnType = CoroutineScope::class)
                  .build(),
                FunSpec
                  .builder("provides${scope.simpleName}CoroutineScope")
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(Provides::class).build(),
                      AnnotationSpec.builder(SingleIn::class).addMember(scopeArgument).build(),
                      AnnotationSpec.builder(FlowCoroutineScope::class).addMember(scopeArgument).build(),
                    )
                  ).addStatement(
                    "return %M(\"${scope.simpleName}\")",
                    MemberName("com.kmpproject.core.domain", "createCoroutineScope"),
                  ).returns(returnType = CoroutineScope::class)
                  .build(),
              )
            ).build()
        )
      }.build()
  }
}

internal fun baseChildComponentConvention(scope: ClassName): ClassName {
  return ClassName(scope.packageName, "Base${scope.simpleName}FlowComponent")
}

internal fun ClassName.asCodeBlock(): CodeBlock {
  return CodeBlock.builder().add(CodeBlock.of("%T::class", this)).build()
}

internal fun KSClassDeclaration.getScope(resolver: Resolver): ClassName {
  val mergeSubcomponentAnnotation = MergeSubcomponent::class
    .qualifiedName
    ?.let(resolver::getKSNameFromString)
    ?.let(resolver::getClassDeclarationByName)
  val contributesSubcomponent = ContributesSubcomponent::class
    .qualifiedName
    ?.let(resolver::getKSNameFromString)
    ?.let(resolver::getClassDeclarationByName)
  return annotations
    .toList()
    .first { annotation ->
      val resolved = annotation.annotationType.resolve()
      resolved == mergeSubcomponentAnnotation?.asStarProjectedType() ||
        resolved == contributesSubcomponent?.asStarProjectedType()
    }.arguments
    .first()
    .value
    ?.let { it as? KSType }
    ?.toClassName()
    ?: error("${simpleName.asString()} has no scope annotation, check declaration")
}

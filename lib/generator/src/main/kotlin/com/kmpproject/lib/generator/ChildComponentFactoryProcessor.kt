package com.kmpproject.lib.generator

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.kmpproject.lib.annotation.ChildComponent
import com.kmpproject.lib.annotation.MergeSubcomponent
import com.kmpproject.lib.annotation.MergedViewModels
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.UUID

internal class ChildComponentFactoryProcessor(
  private val env: SymbolProcessorEnvironment,
) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val declarations = resolver
      .getSymbolsWithAnnotation(MergeSubcomponent::class.asClassName().reflectionName())
      .plus(resolver.getSymbolsWithAnnotation(ContributesSubcomponent::class.asClassName().reflectionName()))
      .minus(resolver.getSymbolsWithAnnotation(ChildComponent::class.asClassName().reflectionName()).toSet())
      .filterIsInstance<KSClassDeclaration>()
    discoverAndGenerateComponentMappings(
      resolver = resolver,
      declarations = declarations
    )
    return emptyList()
  }

  @OptIn(KspExperimental::class)
  fun discoverAndGenerateComponentMappings(
    resolver: Resolver,
    declarations: Sequence<KSClassDeclaration>,
  ) {
    if (declarations.count() > 0) {
      val discoveredMappings = declarations
        .flatMap { declaration ->
          declaration.declarations
            .filterIsInstance<KSFunctionDeclaration>()
            .mapNotNull { it.returnType?.resolve()?.declaration }
            .filter { it.isAnnotationPresent(MergeSubcomponent::class) }
            .filterIsInstance<KSClassDeclaration>()
            .map { declaration to it }
        }.groupBy { (parent, _) -> parent }
        .mapValues { entry -> entry.value.map { it.second } }
      generateCode(
        resolver = resolver,
        componentMappings = discoveredMappings
      )
    }
  }

  private fun generateCode(
    resolver: Resolver,
    componentMappings: Map<KSClassDeclaration, List<KSClassDeclaration>>,
  ) {
    componentMappings
      .entries
      .forEach { (parentDeclaration, childDeclarations) ->
        childDeclarations.forEach { childDeclaration ->
          buildFlowComponent(
            resolver = resolver,
            parent = parentDeclaration,
            child = childDeclaration,
          ).writeTo(
            codeGenerator = env.codeGenerator,
            dependencies = Dependencies(
              aggregating = false,
              parentDeclaration.containingFile!!
            )
          )
        }
      }
  }

  private fun buildFlowComponent(
    resolver: Resolver,
    parent: KSClassDeclaration,
    child: KSClassDeclaration,
  ): FileSpec {
    val childScope = child.getScope(resolver)
    val parentScope = parent.getScope(resolver)

    val bindingComponentName = parentChildComponentBindingConvention(parent.toClassName())
    val factoryName = ClassName(parent.toClassName().packageName, bindingComponentName.simpleName, "Factory")

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

    val childScopeArgument = childScope.asCodeBlock()
    val parentScopeArgument = parentScope.asCodeBlock()

    return FileSpec
      .builder(parent.toClassName().packageName, bindingComponentName.simpleName)
      .apply {
        addType(
          TypeSpec
            .interfaceBuilder(bindingComponentName)
            .addSuperinterface(baseChildComponentConvention(childScope))
            .addAnnotations(
              listOf(
                AnnotationSpec
                  .builder(SingleIn::class)
                  .addMember(childScope.asCodeBlock())
                  .build(),
                AnnotationSpec
                  .builder(ChildComponent::class)
                  .addMember(childScope.asCodeBlock())
                  .build(),
                AnnotationSpec
                  .builder(ContributesSubcomponent::class)
                  .addMember(childScope.asCodeBlock())
                  .build(),
              )
            ).addType(
              TypeSpec
                .interfaceBuilder(factoryName)
                .addAnnotations(
                  listOf(
                    AnnotationSpec
                      .builder(ContributesSubcomponent.Factory::class)
                      .addMember(parentScope.asCodeBlock())
                      .build(),
                  )
                ).addFunctions(
                  listOf(
                    FunSpec
                      .builder("create${bindingComponentName.simpleName}")
                      .addModifiers(KModifier.ABSTRACT)
                      .returns(bindingComponentName)
                      .build(),
                    FunSpec
                      .builder("provide${child.toClassName().simpleName}")
                      .addAnnotations(
                        listOf(
                          AnnotationSpec.builder(Provides::class).build(),
                        )
                      ).addParameter(ParameterSpec.builder("factory", factoryName).build())
                      .addStatement("return factory.create${bindingComponentName.simpleName}()")
                      .returns(child.toClassName())
                      .build(),
                  )
                ).build()
            ).addFunctions(
              listOf(
                FunSpec
                  .builder("parentViewModelProviders")
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(Provides::class).build(),
                      AnnotationSpec.builder(MergedViewModels::class).addMember(childScopeArgument).build(),
                    )
                  ).addParameter(
                    ParameterSpec
                      .builder("parentViewModelProviders", viewModelProviders)
                      .addAnnotation(
                        AnnotationSpec.builder(MergedViewModels::class).addMember(parentScopeArgument).build()
                      ).build(),
                  ).addStatement("return parentViewModelProviders + viewModelProviders()")
                  .returns(returnType = viewModelProviders)
                  .build(),
                FunSpec
                  .builder("flowViewModelProviders")
                  .addModifiers(KModifier.ABSTRACT, KModifier.OVERRIDE)
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(MergedViewModels::class).addMember(childScopeArgument).build(),
                    )
                  ).returns(returnType = viewModelProviders)
                  .build(),
                FunSpec
                  .builder("parentAssistedViewModelProviders")
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(Provides::class).build(),
                      AnnotationSpec.builder(MergedViewModels::class).addMember(childScopeArgument).build(),
                    )
                  ).addParameter(
                    ParameterSpec
                      .builder("parentViewModelProviders", assistedViewModelProviders)
                      .addAnnotation(
                        AnnotationSpec.builder(MergedViewModels::class).addMember(parentScopeArgument).build()
                      ).build(),
                  ).addStatement("return parentViewModelProviders + assistedViewModelProviders()")
                  .returns(returnType = assistedViewModelProviders)
                  .build(),
                FunSpec
                  .builder("assistedFlowViewModelProviders")
                  .addModifiers(KModifier.ABSTRACT, KModifier.OVERRIDE)
                  .addAnnotations(
                    listOf(
                      AnnotationSpec.builder(MergedViewModels::class).addMember(childScopeArgument).build(),
                    )
                  ).returns(returnType = assistedViewModelProviders)
                  .build(),
              )
            ).build()
        )
      }.build()
  }
}

// !Attention: potential build problem!
// Previously we used to produce human-readable names, like CurrenciesInForm, but with deeply nested flow components
// latest kotlin/gradle build will fail with FileNameTooLong during compilation for Flows like this one:
// Currencies -> Form -> Setting -> Profile -> Home -> Authorized -> Foreground -> App
// which produces very long string:
// CurrenciesInFormFinalFormInSettingsFinalSettingsInProfileFinalProfileInPaymentsFinalPaymentsInCreditsFinal...
// It is not possible to shorten it, since all the "Inject", "Final" and "Kotlin" strings are generated by kotlin-inject
// On the other hand, neither it is possible to fix the issue by updating kotlin-inject:
// not only recent versions are not fixing the issue, but also introduce another problems related to Component nesting.
// The only feasible solution seems to be migrating to metro di framework, which now is stable and enough feature rich
internal fun parentChildComponentBindingConvention(
  parent: ClassName,
): ClassName {
  return ClassName(
    parent.packageName,
    randomString(chars = 5).uppercase()
  )
}

private fun randomString(chars: Int): String {
  return UUID.randomUUID().toString().take(chars)
}

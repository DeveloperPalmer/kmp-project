package com.kmpproject.lib.generator

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

@AutoService(SymbolProcessorProvider::class)
@Suppress("unused")
class KotlinInjectExtensionSymbolProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    return CompositeSymbolProcessor(
      symbolProcessors = setOf(
        ChildComponentFactoryProcessor(environment),
        ViewModelBindingProcessor(environment),
        FlowComponentBindingProcessor(environment),
      )
    )
  }
}

class CompositeSymbolProcessor(
  symbolProcessors: Collection<SymbolProcessor>,
) : SymbolProcessor {
  private val symbolProcessors = symbolProcessors.sortedBy { it::class.qualifiedName }

  override fun process(resolver: Resolver): List<KSAnnotated> {
    return symbolProcessors.flatMap { it.process(resolver) }
  }

  override fun finish() {
    symbolProcessors.forEach { it.finish() }
  }

  override fun onError() {
    symbolProcessors.forEach { it.onError() }
  }
}

package com.kmpproject.core.routing.di

import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.ViewModelProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import me.tatarka.inject.compiler.ksp.InjectProcessorProvider
import java.io.File
import java.io.OutputStream
import kotlin.test.assertEquals
import com.kmpproject.lib.generator.KotlinInjectExtensionSymbolProcessorProvider as GeneratorSymbolProcessorProvider
import software.amazon.lastmile.kotlin.inject.anvil.KotlinInjectExtensionSymbolProcessorProvider as AnvilSymbolProcessorProvider

// Same KSP chain as shared-convention gives every shared module: kotlin-inject, kotlin-inject-anvil and the generator
internal fun compile(
  vararg sources: SourceFile,
  classpath: File? = null,
): JvmCompilationResult {
  return KotlinCompilation()
    .apply {
      this.sources = sources.toList()
      inheritClassPath = true
      classpaths = listOfNotNull(classpath)
      messageOutputStream = OutputStream.nullOutputStream()
      configureKsp {
        symbolProcessorProviders += listOf(
          InjectProcessorProvider(),
          AnvilSymbolProcessorProvider(),
          GeneratorSymbolProcessorProvider(),
        )
      }
    }.compile()
}

internal fun JvmCompilationResult.assertCompiled(): JvmCompilationResult {
  assertEquals(KotlinCompilation.ExitCode.OK, exitCode, messages)
  return this
}

internal fun JvmCompilationResult.foregroundComponent(): Any {
  return classLoader
    .loadClass("fixture.ForegroundKt")
    .getMethod("createForegroundComponent")
    .invoke(null)
}

// Fixture classes are compiled while the tests run, so their members are reached reflectively
internal fun Any.call(method: String): Any {
  return javaClass.getMethod(method).invoke(this)
}

internal fun Any.flow(accessor: String): FlowComponent {
  return call(accessor) as FlowComponent
}

internal fun FlowComponent.viewModel(key: String): Any {
  return flowViewModelProviders().single { it.key == key }.factory()
}

internal fun FlowComponent.assistedViewModel(
  key: String,
  vararg params: Any?,
): Any {
  return assistedFlowViewModelProviders().single { it.key == key }.factory.build(*params)
}

// Placeholders only keep kotlin-inject multibinding sets non-empty: https://github.com/evant/kotlin-inject/issues/249
internal const val PLACEHOLDER_KEY = "empty"

@JvmName("viewModelKeys")
internal fun Set<ViewModelProvider>.keys(): Set<String> {
  return mapTo(mutableSetOf()) { it.key } - PLACEHOLDER_KEY
}

@JvmName("assistedViewModelKeys")
internal fun Set<AssistedViewModelProvider>.keys(): Set<String> {
  return mapTo(mutableSetOf()) { it.key } - PLACEHOLDER_KEY
}

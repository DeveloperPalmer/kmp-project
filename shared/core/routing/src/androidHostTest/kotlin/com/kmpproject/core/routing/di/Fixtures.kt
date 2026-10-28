package com.kmpproject.core.routing.di

import com.tschuchort.compiletesting.SourceFile

internal const val MAIN_VIEW_MODEL = "fixture.MainViewModel"
internal const val DETAILS_VIEW_MODEL = "fixture.DetailsViewModel"
internal const val NESTED_VIEW_MODEL = "fixture.NestedViewModel"
internal const val SIBLING_VIEW_MODEL = "fixture.SiblingViewModel"
internal const val SECOND_VIEW_MODEL = "fixture.SecondViewModel"

// Foreground → AppFlow → Nested + Sibling, compiled once for all tests
internal object FlowTree {
  private val compilation by lazy { compile(foregroundSource(), flowTreeSource).assertCompiled() }

  fun foregroundComponent(): Any = compilation.foregroundComponent()
}

// Mirrors core:component: ForegroundComponent with the root placeholders and its platform subcomponent
internal fun foregroundSource(redeclareAccessor: Boolean = true): SourceFile {
  val accessor = if (redeclareAccessor) "override fun appFlowComponent(): AppFlowComponent" else ""
  return SourceFile.kotlin(
    "Foreground.kt",
    """
      package fixture

      import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
      import com.kmpproject.core.ui.viewmodel.ViewModelProvider
      import com.kmpproject.core.ui.viewmodel.emptyAssistedViewModelProvider
      import com.kmpproject.core.ui.viewmodel.emptyViewModelProvider
      import com.kmpproject.lib.annotation.MergedViewModels
      import me.tatarka.inject.annotations.IntoSet
      import me.tatarka.inject.annotations.Provides
      import software.amazon.lastmile.kotlin.inject.anvil.AppScope
      import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
      import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
      import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

      interface ForegroundScope

      @SingleIn(AppScope::class)
      @MergeComponent(AppScope::class)
      abstract class TestAppComponent {
        abstract fun foregroundComponentFactory(): TestForegroundComponent.Factory
      }

      interface ForegroundComponent {
        fun appFlowComponent(): AppFlowComponent

        @Provides
        @IntoSet
        fun emptyMergedViewModelProviders(): @MergedViewModels(ForegroundScope::class) ViewModelProvider {
          return emptyViewModelProvider
        }

        @Provides
        @IntoSet
        fun emptyMergedAssistedViewModelProviders(): @MergedViewModels(ForegroundScope::class) AssistedViewModelProvider {
          return emptyAssistedViewModelProvider
        }
      }

      @SingleIn(ForegroundScope::class)
      @ContributesSubcomponent(ForegroundScope::class)
      interface TestForegroundComponent : ForegroundComponent {
        @ContributesSubcomponent.Factory(AppScope::class)
        interface Factory {
          fun create(): TestForegroundComponent
        }

        $accessor
      }

      fun createForegroundComponent(): TestForegroundComponent {
        return TestAppComponent::class.create().foregroundComponentFactory().create()
      }
    """,
  )
}

internal val flowTreeSource = SourceFile.kotlin(
  "FlowTree.kt",
  """
    package fixture

    import com.kmpproject.core.routing.di.FlowComponent
    import com.kmpproject.core.ui.mvi.BaseViewModel
    import com.kmpproject.lib.annotation.MergeSubcomponent
    import com.kmpproject.lib.annotation.ViewModel
    import me.tatarka.inject.annotations.Assisted
    import me.tatarka.inject.annotations.Inject
    import org.orbitmvi.orbit.orbitContainer
    import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

    interface AppFlowScope

    interface NestedFlowScope

    interface SiblingFlowScope

    @MergeSubcomponent(AppFlowScope::class)
    interface AppFlowComponent : FlowComponent {
      fun nestedFlowComponent(): NestedFlowComponent

      fun siblingFlowComponent(): SiblingFlowComponent
    }

    @MergeSubcomponent(NestedFlowScope::class)
    interface NestedFlowComponent : FlowComponent

    @MergeSubcomponent(SiblingFlowScope::class)
    interface SiblingFlowComponent : FlowComponent

    @Inject
    @SingleIn(AppFlowScope::class)
    class AppFlowRepository

    // AppFlowScope has both plain and assisted view models
    @Inject
    @ViewModel(AppFlowScope::class)
    class MainViewModel(
      val repository: AppFlowRepository,
    ) : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }

    @Inject
    @ViewModel(AppFlowScope::class)
    class DetailsViewModel(
      @Assisted val title: String,
      val repository: AppFlowRepository,
      @Assisted val id: Int,
    ) : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }

    // NestedFlowScope has plain view models only
    @Inject
    @ViewModel(NestedFlowScope::class)
    class NestedViewModel(
      val repository: AppFlowRepository,
    ) : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }

    // SiblingFlowScope has assisted view models only
    @Inject
    @ViewModel(SiblingFlowScope::class)
    class SiblingViewModel(
      @Assisted val title: String,
    ) : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }
  """,
)

// AppFlow without view models, for scenarios that bring their own view models or none
internal val appFlowSource = SourceFile.kotlin(
  "AppFlow.kt",
  """
    package fixture

    import com.kmpproject.core.routing.di.FlowComponent
    import com.kmpproject.lib.annotation.MergeSubcomponent

    interface AppFlowScope

    @MergeSubcomponent(AppFlowScope::class)
    interface AppFlowComponent : FlowComponent
  """,
)

internal val mainViewModelSource = SourceFile.kotlin(
  "MainViewModel.kt",
  """
    package fixture

    import com.kmpproject.core.ui.mvi.BaseViewModel
    import com.kmpproject.lib.annotation.ViewModel
    import me.tatarka.inject.annotations.Inject
    import org.orbitmvi.orbit.orbitContainer

    @Inject
    @ViewModel(AppFlowScope::class)
    class MainViewModel : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }
  """,
)

internal val secondViewModelSource = SourceFile.kotlin(
  "SecondViewModel.kt",
  """
    package fixture

    import com.kmpproject.core.ui.mvi.BaseViewModel
    import com.kmpproject.lib.annotation.ViewModel
    import me.tatarka.inject.annotations.Inject
    import org.orbitmvi.orbit.orbitContainer

    @Inject
    @ViewModel(AppFlowScope::class)
    class SecondViewModel : BaseViewModel<Unit, Nothing>() {
      override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
    }
  """,
)

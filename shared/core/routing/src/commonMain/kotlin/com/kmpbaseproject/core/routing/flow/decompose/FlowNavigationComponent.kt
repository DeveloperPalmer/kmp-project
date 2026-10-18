package com.kmpbaseproject.core.routing.flow.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kmpbaseproject.core.routing.di.FlowComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

@Stable
abstract class FlowNavigationComponent<Config : Any, Child : Node>(
  protected val context: ComponentContext,
) : FlowNode<Config>(), BackHandlerOwner by context {
  abstract val component: FlowComponent

  abstract fun initialConfig(): List<Config>

  // The stack is saved with the state of the Activity, so every config has to be serializable
  protected abstract val configSerializer: KSerializer<Config>

  protected abstract val childFactory: (Config, ComponentContext) -> Child

  protected val navigation = StackNavigation<Config>()

  // Orbit runs intents on Dispatchers.Default, Decompose navigates on the main thread
  private val eventScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

  init {
    context.lifecycle.doOnDestroy {
      eventScope.cancel()
    }
  }

  // There is an initialization conflict between stack and childFactory fields,
  // using lazy as a workaround to super class field initialization order problem
  val stack: Value<ChildStack<*, Child>> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    context.childStack(
      source = navigation,
      serializer = configSerializer,
      initialStack = { initialConfig() },
      handleBackButton = true,
    ) { config, componentContext ->
      val child = childFactory(config, componentContext)
      if (child is Screen) {
        val events = eventScope.launch {
          child.viewModel.events.collect { event -> dispatch(event) }
        }
        componentContext.lifecycle.doOnDestroy {
          events.cancel()
        }
      }
      if (child is Flow) {
        adopt(child)
      }
      child
    }
  }

  override fun navigateTo(config: Config) {
    navigation.pushToFront(config)
  }

  override fun navigateBack() {
    navigation.pop()
  }
}

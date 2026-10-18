package com.kmpbaseproject.core.routing.flow.decompose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.kmpbaseproject.core.routing.di.FlowComponent
import com.kmpbaseproject.core.ui.mvi.BaseViewModel
import kotlinx.coroutines.cancel
import kotlin.reflect.KClass

// Everything below lives in the InstanceKeeper of a component context: it outlives the Activity recreation
// and is destroyed together with the context, i.e. when its screen leaves the stack or the Activity finishes

fun <VM : BaseViewModel<*, *>> ComponentContext.retainedViewModel(modelClass: KClass<VM>, create: () -> VM): VM {
  val instance = instanceKeeper.getOrCreate(modelClass) { ViewModelStoreInstance() }
  val factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T = create() as T
  }
  return ViewModelProvider.create(instance.store, factory)[modelClass]
}

// The DI graph of a flow has to live as long as its view models: they keep models of this graph.
// Both die when the flow is really gone, not with the Activity
fun <C : FlowComponent> ComponentContext.retainedFlowComponent(create: () -> C): C {
  return instanceKeeper.getOrCreate(FlowComponentKey) { FlowComponentInstance(create()) }.component
}

private class ViewModelStoreInstance : InstanceKeeper.Instance {
  val store = ViewModelStore()

  override fun onDestroy() {
    store.clear()
  }
}

private class FlowComponentInstance<C : FlowComponent>(val component: C) : InstanceKeeper.Instance {
  override fun onDestroy() {
    component.coroutineScope().cancel()
  }
}

private object FlowComponentKey

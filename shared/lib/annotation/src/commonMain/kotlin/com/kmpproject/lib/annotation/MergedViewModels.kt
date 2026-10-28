package com.kmpproject.lib.annotation

import me.tatarka.inject.annotations.Qualifier
import kotlin.reflect.KClass

/**
 * Technical annotation used to allow for injection of VMs registered in parent FlowScope into child FlowScope,
 * see ChildComponentFactoryProcessor
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MergedViewModels(val scope: KClass<*>)

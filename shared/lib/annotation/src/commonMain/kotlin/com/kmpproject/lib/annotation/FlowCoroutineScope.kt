package com.kmpproject.lib.annotation

import me.tatarka.inject.annotations.Qualifier
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class FlowCoroutineScope(val scope: KClass<*>)

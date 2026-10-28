package com.kmpproject.lib.annotation

import me.tatarka.inject.annotations.Qualifier
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ScopedViewModel(val value: KClass<out Any>)

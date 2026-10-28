package com.kmpproject.lib.annotation

import software.amazon.lastmile.kotlin.inject.anvil.extend.ContributingAnnotation
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContributingAnnotation
annotation class ViewModel(val scope: KClass<*>)

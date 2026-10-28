package com.kmpproject.lib.annotation

import software.amazon.lastmile.kotlin.inject.anvil.extend.ContributingAnnotation
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContributingAnnotation
annotation class MergeSubcomponent(val scope: KClass<*>)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContributingAnnotation
annotation class ChildComponent(val scope: KClass<*>)

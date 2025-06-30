package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <A> DelegateProvider(block: (ref: Any?, prop: KProperty<*>) -> ReadOnlyProperty<Any?, A>) =
    object : DelegateProvider<A> {
        override fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, A> =
            block(thisRef, prop)
    }

interface DelegateProvider<A> {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, A>
}
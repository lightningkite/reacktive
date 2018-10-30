package com.lightningkite.reacktive.property


import com.lightningkite.reacktive.Event
import kotlin.reflect.KProperty

/**
 * A property that can be observed.
 * Created by josep on 1/28/2016.
 */
interface ObservableProperty<T>: Event<T> {

    /**
     * The current value of the observable property.
     */
    val value: T

    /**
     * Simply retrieves the value.
     * Used for property delegates.
     */
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): T {
        return value
    }
}
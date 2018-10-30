package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.Event


/**
 * An observable that doesn't have to be set at its creation.
 * Created by jivie on 2/11/16.
 */
open class LateInitObservableProperty<T : Any> : MutableObservableProperty<T>, Event<T> by ArrayList() {

    var internalValue: T? = null
    override var value: T
        get() = internalValue ?: throw IllegalStateException("Value not set.")
        set(value) {
            internalValue = value
            for(callback in this){
                callback.invoke(value)
            }
        }
}
package com.lightningkite.reacktive.property


import com.lightningkite.reacktive.Event
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1

/**
 * Creates an observable property out of a reference to a property.
 * Note that for the observable to update, you *must* modify the reference through this observable.
 * Created by jivie on 2/22/16.
 */
class ReferenceObservableProperty<T>(
    val getterFun: () -> T,
    val setterFun: (T) -> Unit
) : MutableObservableProperty<T>, Event<T> by ArrayList() {

    override var value: T
        get() = getterFun()
        set(value) {
            setterFun(value)
            for(callback in this){
                callback.invoke(value)
            }
        }
}
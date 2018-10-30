package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.Event


/**
 * A standard observable property.
 * Simply is a box for a value that can be read or set.
 * Upon being set, it will call every listener it is given.
 *
 * Created by jivie on 1/19/16.
 */
open class StandardObservableProperty<T>(
        initValue: T
) : MutableObservableProperty<T>, Event<T> by ArrayList() {

    override var value: T = initValue
        set(value) {
            field = value
            for(callback in this){
                callback.invoke(value)
            }
        }
}
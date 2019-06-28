package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.DebugEvent
import com.lightningkite.reacktive.Event


/**
 * A standard observable property.
 * Simply is a box for a value that can be read or set.
 * Upon being set, it will call every listener it is given.
 *
 * Created by jivie on 1/19/16.
 */
open class DebugObservableProperty<T>(
        initValue: T
) : MutableObservableProperty<T>, Event<T> by DebugEvent() {


    override var value: T = initValue
        set(value) {
            field = value
            for(callback in this){
                callback.invoke(value)
            }
        }
}
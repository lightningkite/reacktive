package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.StandardEvent


/**
 * A standard observable property.
 * Simply is a box for a value that can be read or set.
 * Upon being set, it will call every listener it is given.
 *
 * Created by jivie on 1/19/16.
 */
open class StandardObservableProperty<T>(
        value: T
) : MutableObservableProperty<T> {
    private val _onChange = StandardEvent<T>()
    override val onChange: Event<T> get() = _onChange

    override var value: T = value
        set(value) {
            field = value
            _onChange.invoke(value)
        }
}
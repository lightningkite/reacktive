package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.InvokableEvent
import com.lightningkite.reacktive.event.StandardEvent


/**
 * A standard observable property.
 * Simply is a box for a value that can be read or set.
 * Upon being set, it will call every listener it is given.
 *
 * [onChange] is the underlying invokable event used to push updates.  Default works great; use another if you need it.
 *
 * Created by jivie on 1/19/16.
 */
open class StandardObservableProperty<T>(
        value: T,
        override val onChange: InvokableEvent<T> = StandardEvent<T>()
) : MutableObservableProperty<T> {

    override var value: T = value
        set(value) {
            field = value
            onChange.invoke(value)
        }
}
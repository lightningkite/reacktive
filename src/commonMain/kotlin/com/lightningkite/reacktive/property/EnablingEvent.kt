package com.lightningkite.reacktive.property

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.StandardEvent
import com.lightningkite.reacktive.invokeAll


/**
 * A standard observable property.
 * Simply is a box for a value that can be read or set.
 * Upon being set, it will call every listener it is given.
 *
 * Created by jivie on 1/19/16.
 */
open class EnablingEvent<T>(
        value: T
) : Event<T> {
    private val _lifecycle = StandardObservableProperty<Boolean>(false)
    val anyListeners: Lifecycle get() = _lifecycle

    private val listeners = ArrayList<(T) -> Unit>()
    override fun listen(listener: (T) -> Unit): Closeable {
        _lifecycle.value = true
        listeners.add(listener)
        return CloseableLambda {
            listeners.remove(listener)
            _lifecycle.value = listeners.isNotEmpty()
        }
    }
    fun invokeAll(value: T) {
        listeners.invokeAll(value)
    }
}
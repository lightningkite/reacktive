package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.invokeAll

class StandardEvent<T>: Event<T> {
    private val listeners = ArrayList<(T) -> Unit>()
    val nobodyListening: Boolean get() = listeners.isEmpty()
    override fun listen(listener: (T) -> Unit): Closeable {
        listeners.add(listener)
        return CloseableLambda { listeners.remove(listener) }
    }
    fun invokeAll(value: T) {
        listeners.invokeAll(value)
    }
    inline fun invokeAllLazy(make: ()->T) {
        if(!nobodyListening) return
        val value = make()
        invokeAll(value)
    }
}
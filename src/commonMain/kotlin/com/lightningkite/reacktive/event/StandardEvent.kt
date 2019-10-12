package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.invokeAll
import kotlin.jvm.JvmName

class StandardEvent<T>: InvokableEvent<T> {
    private val listeners = ArrayList<(T) -> Unit>()
    override val nobodyListening: Boolean get() = listeners.isEmpty()
    override fun listen(listener: (T) -> Unit): Closeable {
        listeners.add(listener)
        return CloseableLambda { listeners.remove(listener) }
    }
    override operator fun invoke(value: T) {
        listeners.invokeAll(value)
    }
}
package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.invokeAll
import kotlin.jvm.JvmName

/**
 * An event that is restricted to having one listener.
 * DO NOT USE THIS UNLESS YOU KNOW THERE IS ONLY EVER GOING TO BE ONE.
 */
class SingleListenerEvent<T>: InvokableEvent<T> {
    private var listener: ((T)->Unit)? = null
    override val nobodyListening: Boolean get() = listener == null
    override fun listen(listener: (T) -> Unit): Closeable {
        if(this.listener != null) throw IllegalStateException("Only one listener can exist for SingleListenerEvent")
        this.listener = listener
        return CloseableLambda { this.listener = null }
    }
    override operator fun invoke(value: T) {
        listener?.invoke(value)
    }
}
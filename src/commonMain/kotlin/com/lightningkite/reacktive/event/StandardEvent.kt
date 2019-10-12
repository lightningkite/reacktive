package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.invokeAll
import kotlin.jvm.JvmName

class StandardEvent<T>: Event<T> {
    private val listeners = ArrayList<(T) -> Unit>()
    val nobodyListening: Boolean get() = listeners.isEmpty()
    override fun listen(listener: (T) -> Unit): Closeable {
        listeners.add(listener)
        return CloseableLambda { listeners.remove(listener) }
    }
    operator fun invoke(value: T) {
        listeners.invokeAll(value)
    }
    @JvmName("invokeLazy")
    inline operator fun invoke(make: ()->T) {
        if(!nobodyListening) return
        val value = make()
        this.invoke(value)
    }
    @Deprecated("Use invoke() instead", ReplaceWith("invoke(value)"))
    fun invokeAll(value: T) = invoke(value)
    @Deprecated("Use invoke() instead", ReplaceWith("invoke(make)"))
    inline fun invokeAllLazy(make: ()->T) = invoke(make)
}
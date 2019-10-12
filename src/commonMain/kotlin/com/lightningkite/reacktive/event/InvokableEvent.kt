package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import kotlin.jvm.JvmName

interface InvokableEvent<T>: Event<T> {
    operator fun invoke(value: T)
    val nobodyListening: Boolean
}

@Deprecated("Use invoke() instead", ReplaceWith("invoke(value)"))
fun <T> InvokableEvent<T>.invokeAll(value: T) = invoke(value)

@JvmName("invokeLazy")
inline operator fun <T> InvokableEvent<T>.invoke(make: ()->T) {
    if(!nobodyListening) return
    val value = make()
    this.invoke(value)
}
@Deprecated("Use invoke() instead", ReplaceWith("invoke(make)"))
inline fun <T> InvokableEvent<T>.invokeAllLazy(make: ()->T) = invoke(make)
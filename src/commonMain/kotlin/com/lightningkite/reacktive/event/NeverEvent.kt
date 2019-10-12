package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.invokeAll

fun <T> NeverEvent(): Event<T> = NeverEventInstance as Event<T>
object NeverEventInstance: Event<Unit> {
    override fun listen(listener: (Unit) -> Unit): Closeable = CloseableLambda {}
}
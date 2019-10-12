package com.lightningkite.reacktive.property

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.StandardEvent
import com.lightningkite.reacktive.invokeAll

fun <T> Event<T>.withListenerCounter(counter: MutableObservableProperty<Int>): Event<T> = object : Event<T> {
    override fun listen(listener: (T) -> Unit): Closeable {
        counter.value++
        val closeable = this@withListenerCounter.listen(listener)
        return CloseableLambda {
            closeable.close()
            counter.value--
        }
    }
}
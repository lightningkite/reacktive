package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable

interface Event<out T> {
    fun listen(listener: (T) -> Unit): Closeable
}

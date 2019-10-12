package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable

interface Event<T> {
    fun listen(listener: (T) -> Unit): Closeable
}

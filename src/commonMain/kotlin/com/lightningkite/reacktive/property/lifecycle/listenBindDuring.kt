package com.lightningkite.reacktive.property.lifecycle

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty

fun <T> Event<T>.listen(
        during: Lifecycle,
        listener: (T)->Unit
): Closeable {
    var subCloseable: Closeable? = null
    val mainCloseable = during.openCloseBinding(
            onOpen = { subCloseable = this@listen.listen(listener) },
            onClose = { subCloseable?.close() }
    )
    return CloseableLambda {
        mainCloseable.close()
        subCloseable?.close()
    }
}

fun <T> ObservableProperty<T>.bind(
        during: Lifecycle,
        listener: (T)->Unit
): Closeable {
    listener.invoke(value)
    return this.onChange.listen(during, listener)
}
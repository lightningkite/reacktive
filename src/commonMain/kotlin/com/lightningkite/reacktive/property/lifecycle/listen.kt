package com.lightningkite.reacktive.property.lifecycle

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty

fun <T> Lifecycle.listen(
        event: Event<T>,
        item: (T)->Unit
): Closeable {
    var subCloseable: Closeable? = null
    val mainCloseable = openCloseBinding(
            onOpen = { subCloseable = event.listen(item) },
            onClose = { subCloseable?.close() }
    )
    return CloseableLambda {
        mainCloseable.close()
        subCloseable?.close()
    }
}

fun <T> Lifecycle.bind(
        property: ObservableProperty<T>,
        item: (T)->Unit
): Closeable {
    item.invoke(property.value)
    return listen(property.onChange, item)
}
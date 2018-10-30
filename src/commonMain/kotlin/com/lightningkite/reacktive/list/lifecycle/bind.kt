package com.lightningkite.reacktive.list.lifecycle

import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.list.ObservableList
import com.lightningkite.reacktive.list.ObservableListListenerSet
import com.lightningkite.reacktive.list.addListenerSet
import com.lightningkite.reacktive.list.removeListenerSet
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.lifecycle.bind
import com.lightningkite.reacktive.property.lifecycle.openCloseBinding


fun <T> Lifecycle.bind(observable: ObservableList<T>, listener: (ObservableList<T>) -> Unit) {
    bind(observable.onListUpdate, listener)
}

fun <T> Lifecycle.bind(observable: ObservableList<T>, listenerSet: ObservableListListenerSet<T>) {
    this.openCloseBinding(
            onOpen = { observable.addListenerSet(listenerSet) },
            onClose = { observable.removeListenerSet(listenerSet) }
    )
}
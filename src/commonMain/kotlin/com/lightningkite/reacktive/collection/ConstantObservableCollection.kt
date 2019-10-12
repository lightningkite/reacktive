package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.NeverEvent
import com.lightningkite.reacktive.event.StandardEvent


class ConstantObservableCollection<V>(val wraps: Collection<V>): ObservableCollection<V>, Collection<V> by wraps {
    override val onCollectionAdd: Event<V>
        get() = NeverEvent()
    override val onCollectionChange: Event<Pair<V, V>>
        get() = NeverEvent()
    override val onCollectionRemove: Event<V>
        get() = NeverEvent()
    override val onCollectionReplace: Event<ObservableCollection<V>>
        get() = NeverEvent()
    override val onChange: Event<ObservableCollection<V>>
        get() = NeverEvent()
}

fun <T: Collection<V>, V> T.constant() = ConstantObservableCollection(this)
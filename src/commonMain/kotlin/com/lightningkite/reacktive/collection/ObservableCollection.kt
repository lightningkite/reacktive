package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty


interface ObservableCollection<V> : Collection<V> {
    val onChange: Event<ObservableCollection<V>>
    val onCollectionAdd: Event<V>
    val onCollectionChange: Event<Pair<V, V>>
    val onCollectionRemove: Event<V>
    val onCollectionReplace: Event<ObservableCollection<V>>
}


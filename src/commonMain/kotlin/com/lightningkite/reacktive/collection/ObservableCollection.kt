package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.property.ObservableProperty


interface ObservableCollection<V> : Collection<V> {
    val onCollectionAdd: MutableCollection<(value: V) -> Unit>
    val onCollectionChange: MutableCollection<(old: V, new: V) -> Unit>
    val onCollectionRemove: MutableCollection<(value: V) -> Unit>
    val onCollectionReplace: MutableCollection<(ObservableCollection<V>) -> Unit>

    val onCollectionUpdate: ObservableProperty<ObservableCollection<V>>

}


package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.set.MutableObservableSet
import com.lightningkite.reacktive.set.ObservableSet

interface ObservableMap<K, V> : Map<K, V> {

    override val entries: ObservableSet<out Map.Entry<K, V>>
    override val keys: ObservableSet<K>
    override val values: ObservableCollection<V>

    val onMapPut: MutableCollection<(key: K, hadPrevious: Boolean, previous: V?, new: V) -> Unit>
    val onMapRemove: MutableCollection<(key: K, value: V) -> Unit>
    val onMapReplace: MutableCollection<(ObservableMap<K, V>) -> Unit>
    val onMapUpdate: ObservableProperty<ObservableMap<K, V>>
}


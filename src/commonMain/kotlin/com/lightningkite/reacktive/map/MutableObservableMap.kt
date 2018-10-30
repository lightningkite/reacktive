package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.set.MutableObservableSet

interface MutableObservableMap<K, V> : MutableMap<K, V>, ObservableMap<K, V>{

    override val entries: MutableObservableSet<MutableMap.MutableEntry<K, V>>
    override val keys: MutableObservableSet<K>
    override val values: MutableObservableCollection<V>

    fun replace(map:Map<K, V>)
}
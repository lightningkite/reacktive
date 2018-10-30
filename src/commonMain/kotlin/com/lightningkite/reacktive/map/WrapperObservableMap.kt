package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.property.ObservablePropertyReference
import com.lightningkite.reacktive.property.update
import com.lightningkite.reacktive.set.MutableObservableSet

class WrapperObservableMap<K, V>(val wraps: MutableMap<K, V> = HashMap<K, V>()) : MutableObservableMap<K, V>{

    override val onMapPut: MutableCollection<(key: K, hadPrevious: Boolean, previous: V?, new: V) -> Unit> = ArrayList()
    override val onMapRemove: MutableCollection<(key: K, value: V) -> Unit> = ArrayList()
    override val onMapUpdate = ObservablePropertyReference<ObservableMap<K, V>>({ this }, { replace(it) })
    override val onMapReplace: MutableCollection<(ObservableMap<K, V>) -> Unit> = ArrayList()

    override val entries: MutableObservableSet<MutableMap.MutableEntry<K, V>> = MutableEntryObservableSet(
            parent = this,
            nonObservableEntryIterator = {wraps.entries.iterator()}
    )
    override val keys: MutableObservableSet<K> = MutableKeyObservableSet(
            parent = this,
            nonObservableEntryIterator = {wraps.entries.iterator()}
    )
    override val values: MutableObservableCollection<V> = MutableValueObservableSet(
            parent = this,
            nonObservableEntryIterator = {wraps.entries.iterator()}
    )

    override fun replace(map: Map<K, V>) {
        wraps.clear()
        wraps.putAll(map)
        onMapReplace.invokeAll(this)
        onMapUpdate.update()
    }

    override val size: Int get() = wraps.size

    override fun containsKey(key: K): Boolean = wraps.containsKey(key)

    override fun containsValue(value: V): Boolean = wraps.containsValue(value)

    override fun get(key: K): V? = wraps[key]

    override fun isEmpty(): Boolean = wraps.isEmpty()

    override fun clear() {
        wraps.clear()
        onMapReplace.invokeAll(this)
        onMapUpdate.update()
    }

    override fun put(key: K, value: V): V? {
        val previouslyHad = wraps.containsKey(key)
        val previous = wraps.put(key, value)
        onMapPut.invokeAll(key, previouslyHad, previous, value)
        onMapUpdate.update()
        return previous
    }

    override fun putAll(from: Map<out K, V>) {
        for(entry in from.entries){
            put(entry.key, entry.value)
        }
    }

    override fun remove(key: K): V? {
        val previouslyHad = wraps.containsKey(key)
        return if(previouslyHad){
            val previous = wraps.remove(key)
            onMapRemove.invokeAll(key, previous as V)
            onMapUpdate.update()
            previous
        } else null
    }
}

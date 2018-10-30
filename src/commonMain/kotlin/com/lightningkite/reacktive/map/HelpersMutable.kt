package com.lightningkite.reacktive.map

import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.reacktive.set.MutableObservableSet
import com.lightningkite.reacktive.set.ObservableSet


private data class MutableDummyEntry<K, V>(val parent: MutableMap<K, V>, override val key: K, override val value: V) : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
        val old = value
        parent[key] = newValue
        return old
    }
}

class MutableEntryObservableSet<K, V>(
        val parent: MutableObservableMap<K, V>,
        val nonObservableEntryIterator: () -> MutableIterator<MutableMap.MutableEntry<K, V>>
) : MutableObservableSet<MutableMap.MutableEntry<K, V>> {
    override val size: Int get() = parent.size

    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
        return parent[element.key] == element.value
    }

    override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()

    override val onCollectionAdd: MutableCollection<(value: MutableMap.MutableEntry<K, V>) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (!hadPrevious) callback.invoke(MutableDummyEntry(parent, key, new)) }
    }
    override val onCollectionChange: MutableCollection<(old: MutableMap.MutableEntry<K, V>, new: MutableMap.MutableEntry<K, V>) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (hadPrevious) callback.invoke(MutableDummyEntry(parent, key, previous as V), MutableDummyEntry(parent, key, new)) }
    }
    override val onCollectionRemove: MutableCollection<(value: MutableMap.MutableEntry<K, V>) -> Unit> = parent.onMapRemove.mappingWriteOnly { callback ->
        { key, previous -> callback.invoke(MutableDummyEntry(parent, key, previous)) }
    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<MutableMap.MutableEntry<K, V>>> = parent.onMapUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<MutableMap.MutableEntry<K, V>>) -> Unit> = parent.onMapReplace.mappingWriteOnly { callback ->
        { callback.invoke(this) }
    }


    override fun replace(collection: Collection<MutableMap.MutableEntry<K, V>>)
            = parent.replace(collection.associate { it.key to it.value })

    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
        parent.put(element.key, element.value)
        return true
    }

    override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
        var changed = false
        for (element in elements) {
            changed = changed || add(element)
        }
        return changed
    }

    override fun clear() {
        parent.clear()
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = object : MutableIterator<MutableMap.MutableEntry<K, V>> {
        val underlying = nonObservableEntryIterator()
        var last: MutableMap.MutableEntry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): MutableMap.MutableEntry<K, V> {
            val n = underlying.next()
            last = n
            return MutableDummyEntry(parent, n.key, n.value)
        }

        override fun remove() {
            underlying.remove()
            parent.onMapRemove.invokeAll(last!!.key, last!!.value)
        }
    }

    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
        return if (contains(element)) {
            parent.remove(element.key)
            true
        } else false
    }

    override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
        var removed = false
        for (element in elements) {
            removed = removed || remove(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean {
        throw UnsupportedOperationException()
    }

}

class MutableKeyObservableSet<K, V>(
        val parent: MutableObservableMap<K, V>,
        val nonObservableEntryIterator: () -> MutableIterator<MutableMap.MutableEntry<K, V>>
) : MutableObservableSet<K> {
    override val size: Int get() = parent.size

    override fun contains(element: K): Boolean {
        return parent.containsKey(element)
    }

    override fun containsAll(elements: Collection<K>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()




    override fun replace(collection: Collection<K>) = throw UnsupportedOperationException()

    override fun add(element: K): Boolean = throw UnsupportedOperationException()

    override fun addAll(elements: Collection<K>): Boolean = throw UnsupportedOperationException()

    override fun clear() {
        parent.clear()
    }

    override fun iterator(): MutableIterator<K> = object : MutableIterator<K> {
        val underlying = nonObservableEntryIterator()
        var last: MutableMap.MutableEntry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): K {
            val n = underlying.next()
            last = n
            return n.key
        }

        override fun remove() {
            underlying.remove()
            parent.onMapRemove.invokeAll(last!!.key, last!!.value)
        }
    }

    override fun remove(element: K): Boolean {
        return if (parent.containsKey(element)) {
            parent.remove(element)
            true
        } else false
    }

    override fun removeAll(elements: Collection<K>): Boolean {
        var removed = false
        for (element in elements) {
            removed = removed || remove(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<K>): Boolean {
        throw UnsupportedOperationException()
    }



    override val onCollectionAdd: MutableCollection<(value: K) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (!hadPrevious) callback.invoke(key) }
    }
    override val onCollectionChange: MutableCollection<(old: K, new: K) -> Unit> = ArrayList() //never happens
    override val onCollectionRemove: MutableCollection<(value: K) -> Unit> = parent.onMapRemove.mappingWriteOnly { callback ->
        { key, previous -> callback.invoke(key) }
    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<K>> = parent.onMapUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<K>) -> Unit> = parent.onMapReplace.mappingWriteOnly { callback ->
        { callback.invoke(this) }
    }

}

class MutableValueObservableSet<K, V>(
        val parent: MutableObservableMap<K, V>,
        val nonObservableEntryIterator: () -> MutableIterator<MutableMap.MutableEntry<K, V>>
) : MutableObservableSet<V> {
    override val size: Int get() = parent.size

    override fun contains(element: V): Boolean {
        return parent.containsValue(element)
    }

    override fun containsAll(elements: Collection<V>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()

    override fun replace(collection: Collection<V>) = throw UnsupportedOperationException()

    override fun add(element: V): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<V>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        parent.clear()
    }

    override fun iterator(): MutableIterator<V> = object : MutableIterator<V> {
        val underlying = nonObservableEntryIterator()
        var last: MutableMap.MutableEntry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): V {
            val n = underlying.next()
            last = n
            return n.value
        }

        override fun remove() {
            underlying.remove()
            parent.onMapRemove.invokeAll(last!!.key, last!!.value)
        }
    }

    override fun remove(element: V): Boolean {
        val keyFor = parent.keys.find { parent[it] == element }
        return if (keyFor != null) {
            parent.remove(keyFor)
            true
        } else false
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        //TODO: Can speed up
        var removed = false
        for (element in elements) {
            removed = removed || remove(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        throw UnsupportedOperationException()
    }

    override val onCollectionAdd: MutableCollection<(value: V) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (!hadPrevious) callback.invoke(new) }
    }
    override val onCollectionChange: MutableCollection<(old: V, new: V) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (hadPrevious) callback.invoke(previous as V, new) }
    }
    override val onCollectionRemove: MutableCollection<(value: V) -> Unit> = object : MutableCollection<(V) -> Unit> {
        override val size: Int get() = parent.onMapPut.size + parent.onMapRemove.size

        val mapPut: MutableMap<(V) -> Unit, (K, Boolean, V?, V) -> Unit> = mutableMapOf()
        val mapRemove: MutableMap<(V) -> Unit, (K, V) -> Unit> = mutableMapOf()

        val mapperPut = { callback: (V) -> Unit ->
            { _: K, wasRemoval: Boolean, removed: V?, _: V -> if (wasRemoval) callback.invoke(removed as V) }
        }
        val mapperRemove = { callback: (V) -> Unit ->
            { _: K, removed: V -> callback.invoke(removed) }
        }

        override fun add(element: (V) -> Unit): Boolean {
            val mapped = mapperPut(element)
            mapPut[element] = mapped
            parent.onMapPut.add(mapped)
            val mapped2 = mapperRemove(element)
            mapRemove[element] = mapped2
            parent.onMapRemove.add(mapped2)
            return true
        }

        override fun addAll(elements: Collection<(V) -> Unit>): Boolean {
            elements.forEach { add(it) }
            return true
        }

        override fun remove(element: (V) -> Unit): Boolean {
            return parent.onMapPut.remove(mapPut.remove(element)) and parent.onMapRemove.remove(mapRemove.remove(element))
        }

        override fun removeAll(elements: Collection<(V) -> Unit>): Boolean {
            elements.forEach { remove(it) }
            return true
        }

        override fun retainAll(elements: Collection<(V) -> Unit>): Boolean = throw UnsupportedOperationException()

        override fun contains(element: (V) -> Unit): Boolean = throw IllegalStateException("Not readable")
        override fun containsAll(elements: Collection<(V) -> Unit>): Boolean = throw IllegalStateException("Not readable")
        override fun isEmpty(): Boolean = throw IllegalStateException("Not readable")
        override fun clear() = throw IllegalStateException("Not readable")
        override fun iterator(): MutableIterator<(V) -> Unit> = throw IllegalStateException("Not readable")

    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<V>> = parent.onMapUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<V>) -> Unit> = parent.onMapReplace.mappingWriteOnly { callback ->
        { callback.invoke(this) }
    }

}

package com.lightningkite.reacktive.map

import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.reacktive.set.ObservableSet

private data class DummyEntry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

class EntryObservableSet<K, V>(val parent: ObservableMap<K, V>, val nonObservableEntryIterator: () -> Iterator<Map.Entry<K, V>>) : ObservableSet<Map.Entry<K, V>> {
    override val size: Int get() = parent.size

    override fun contains(element: Map.Entry<K, V>): Boolean {
        return parent[element.key] == element.value
    }

    override fun containsAll(elements: Collection<Map.Entry<K, V>>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()

    override fun iterator(): Iterator<Map.Entry<K, V>> = object : Iterator<Map.Entry<K, V>> {
        val underlying = nonObservableEntryIterator()
        var last: Map.Entry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): Map.Entry<K, V> {
            val n = underlying.next()
            last = n
            return DummyEntry(n.key, n.value)
        }
    }

    override val onCollectionAdd: MutableCollection<(value: Map.Entry<K, V>) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (!hadPrevious) callback.invoke(DummyEntry(key, new)) }
    }
    override val onCollectionChange: MutableCollection<(old: Map.Entry<K, V>, new: Map.Entry<K, V>) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (hadPrevious) callback.invoke(DummyEntry(key, previous as V), DummyEntry(key, new)) }
    }
    override val onCollectionRemove: MutableCollection<(value: Map.Entry<K, V>) -> Unit> = parent.onMapRemove.mappingWriteOnly { callback ->
        { key, previous -> callback.invoke(DummyEntry(key, previous)) }
    }
    override val onCollectionUpdate: ObservableProperty<ObservableCollection<Map.Entry<K, V>>> = parent.onMapUpdate.transform { this }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<Map.Entry<K, V>>) -> Unit> = parent.onMapReplace.mappingWriteOnly { callback ->
        { callback.invoke(this) }
    }

}

class KeyObservableSet<K, V>(val parent: ObservableMap<K, V>, val nonObservableEntryIterator: () -> Iterator<Map.Entry<K, V>>) : ObservableSet<K> {
    override val size: Int get() = parent.size

    override fun contains(element: K): Boolean {
        return parent.containsKey(element)
    }

    override fun containsAll(elements: Collection<K>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()

    override fun iterator(): Iterator<K> = object : Iterator<K> {
        val underlying = nonObservableEntryIterator()
        var last: Map.Entry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): K {
            val n = underlying.next()
            last = n
            return n.key
        }
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

class ValueObservableSet<K, V>(val parent: ObservableMap<K, V>, val nonObservableEntryIterator: () -> Iterator<Map.Entry<K, V>>) : ObservableSet<V> {
    override val size: Int get() = parent.size

    override fun contains(element: V): Boolean {
        return parent.containsValue(element)
    }

    override fun containsAll(elements: Collection<V>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = parent.isEmpty()

    override fun iterator(): Iterator<V> = object : Iterator<V> {
        val underlying = nonObservableEntryIterator()
        var last: Map.Entry<K, V>? = null
        override fun hasNext(): Boolean = underlying.hasNext()
        override fun next(): V {
            val n = underlying.next()
            last = n
            return n.value
        }
    }

    override val onCollectionAdd: MutableCollection<(value: V) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (!hadPrevious) callback.invoke(new) }
    }
    @Suppress("UNCHECKED_CAST")
    override val onCollectionChange: MutableCollection<(old: V, new: V) -> Unit> = parent.onMapPut.mappingWriteOnly { callback ->
        { key, hadPrevious, previous, new -> if (hadPrevious) callback.invoke(previous as V, new) }
    }
    @Suppress("UNCHECKED_CAST")
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

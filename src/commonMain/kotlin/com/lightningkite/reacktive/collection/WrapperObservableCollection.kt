package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.StandardEvent
import com.lightningkite.reacktive.event.invoke

class WrapperObservableCollection<V>(val wraps: MutableCollection<V>): MutableObservableCollection<V> {
    private val _onCollectionAdd = StandardEvent<V>()
    override val onCollectionAdd: Event<V> get() = _onCollectionAdd
    private val _onCollectionChange = StandardEvent<Pair<V, V>>()
    override val onCollectionChange: Event<Pair<V, V>> get() = _onCollectionChange
    private val _onCollectionRemove = StandardEvent<V>()
    override val onCollectionRemove: Event<V> get() = _onCollectionRemove
    private val _onCollectionReplace = StandardEvent<ObservableCollection<V>>()
    override val onCollectionReplace: Event<ObservableCollection<V>> get() = _onCollectionReplace
    private val _onChange = StandardEvent<ObservableCollection<V>>()
    override val onChange: Event<ObservableCollection<V>> get() = _onChange

    override val size: Int get() = wraps.size
    override fun contains(element: V): Boolean = wraps.contains(element)
    override fun containsAll(elements: Collection<V>): Boolean = wraps.containsAll(elements)
    override fun isEmpty(): Boolean = wraps.isEmpty()
    override fun add(element: V): Boolean {
        return if(wraps.add(element)){
            _onCollectionAdd.invoke(element)
            _onChange.invoke(this)
            true
        } else {
            false
        }
    }

    override fun change(old: V, new: V) {
        remove(old)
        add(new)
        _onCollectionChange.invoke { old to new }
        _onChange.invoke(this)
    }

    override fun addAll(elements: Collection<V>): Boolean {
        var added = false
        for(element in elements){
            added = added || add(element)
        }
        return added
    }

    override fun clear() {
        wraps.clear()
        _onCollectionReplace.invoke(this)
        _onChange.invoke(this)
    }

    override fun iterator(): MutableIterator<V> = object : MutableIterator<V> {

        val underlying = wraps.iterator()
        var last: V? = null

        override fun hasNext(): Boolean = underlying.hasNext()

        override fun next(): V {
            val n = underlying.next()
            last = n
            return n
        }

        override fun remove() {
            underlying.remove()
            @Suppress("UNCHECKED_CAST")
            _onCollectionRemove.invoke(last as V)
            _onChange.invoke(this@WrapperObservableCollection)
        }
    }

    override fun remove(element: V): Boolean {
        return if(wraps.remove(element)){
            _onCollectionRemove.invoke(element)
            _onChange.invoke(this)
            true
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        var removed = false
        for(element in elements){
            removed = removed || remove(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        return if(wraps.retainAll(elements)){
            _onCollectionReplace.invoke(this)
            _onChange.invoke(this)
            true
        } else {
            false
        }
    }

    override fun replace(collection: Collection<V>) {
        wraps.clear()
        wraps.addAll(collection)
        _onCollectionReplace.invoke(this)
        _onChange.invoke(this)
    }
}

fun <E> MutableCollection<E>.observable() = WrapperObservableCollection(this)

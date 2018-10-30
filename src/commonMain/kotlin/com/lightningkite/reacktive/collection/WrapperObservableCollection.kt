package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ReferenceObservableProperty
import com.lightningkite.reacktive.property.update

class WrapperObservableCollection<V>(val wraps: MutableCollection<V>): MutableObservableCollection<V> {
    override val size: Int get() = wraps.size
    override fun contains(element: V): Boolean = wraps.contains(element)
    override fun containsAll(elements: Collection<V>): Boolean = wraps.containsAll(elements)
    override fun isEmpty(): Boolean = wraps.isEmpty()
    override fun add(element: V): Boolean {
        return if(wraps.add(element)){
            onCollectionAdd.invokeAll(element)
            onCollectionUpdate.update()
            true
        } else {
            false
        }
    }

    override fun change(old: V, new: V) {
        remove(old)
        add(new)
        onCollectionChange.invokeAll(old, new)
        onCollectionUpdate.update()
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
        onCollectionReplace.invokeAll(this)
        onCollectionUpdate.update()
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
            onCollectionRemove.invokeAll(last as V)
            onCollectionUpdate.update()
        }
    }

    override fun remove(element: V): Boolean {
        return if(wraps.remove(element)){
            onCollectionRemove.invokeAll(element)
            onCollectionUpdate.update()
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
            onCollectionReplace.invokeAll(this)
            onCollectionUpdate.update()
            true
        } else {
            false
        }
    }

    override fun replace(collection: Collection<V>) {
        wraps.clear()
        wraps.addAll(collection)
        onCollectionReplace.invokeAll(this)
        onCollectionUpdate.update()
    }

    override val onCollectionAdd: MutableCollection<(value: V) -> Unit> = ArrayList()
    override val onCollectionChange: MutableCollection<(old: V, new: V) -> Unit> = ArrayList()
    override val onCollectionRemove: MutableCollection<(value: V) -> Unit> = ArrayList()
    override val onCollectionUpdate = ReferenceObservableProperty<ObservableCollection<V>>({ this@WrapperObservableCollection }, { replace(it) })
    override val onCollectionReplace: MutableCollection<(ObservableCollection<V>) -> Unit> = ArrayList()
}

fun <E> MutableCollection<E>.observable() = WrapperObservableCollection(this)

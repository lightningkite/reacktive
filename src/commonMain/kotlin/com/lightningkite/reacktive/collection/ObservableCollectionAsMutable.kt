package com.lightningkite.reacktive.collection

import com.lightningkite.kommon.collection.asMutable
import com.lightningkite.reacktive.property.ObservableProperty


/**
 * Makes an observable collection mutable.
 * Meant to be used in temporary function calls to reduce the number of classes
 */
class ObservableCollectionAsMutable<V>(val source:ObservableCollection<V>) : MutableObservableCollection<V>, ObservableCollection<V> by source {
    override fun change(old: V, new: V) {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun replace(collection: Collection<V>) {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun add(element: V): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun addAll(elements: Collection<V>): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun clear() {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun remove(element: V): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun iterator(): MutableIterator<V> = source.iterator().asMutable()
}

fun <V> ObservableCollection<V>.asMutable():MutableObservableCollection<V> = ObservableCollectionAsMutable(this)


package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.mapping.asMutable

/**
 * Makes an observable collection mutable.
 * Meant to be used in temporary function calls to reduce the number of classes
 */
class ObservableListAsMutable<V>(val source:ObservableList<V>) : MutableObservableList<V>, ObservableList<V> by source {
    override fun add(index: Int, element: V) {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun addAll(index: Int, elements: Collection<V>): Boolean {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun removeAt(index: Int): V {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

    override fun set(index: Int, element: V): V {
        throw UnsupportedOperationException("The underlying collection is not mutable.")
    }

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
    override fun listIterator(): MutableListIterator<V> = source.listIterator().asMutable()
    override fun listIterator(index: Int): MutableListIterator<V> = source.listIterator(index).asMutable()
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<V> = source.subList(fromIndex, toIndex).toMutableList()
}

fun <V> ObservableList<V>.asMutable():MutableObservableList<V> = ObservableListAsMutable(this)


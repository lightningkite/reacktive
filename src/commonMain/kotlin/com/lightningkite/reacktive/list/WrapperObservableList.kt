package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservablePropertyReference
import com.lightningkite.reacktive.property.update

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class WrapperObservableList<E>(
        val collection: MutableList<E> = mutableListOf()
) : MutableObservableList<E> {

    override val onListAdd = HashSet<(E, Int) -> Unit>()
    override val onListChange = HashSet<(E, E, Int) -> Unit>()
    override val onListMove = HashSet<(E, Int, Int) -> Unit>()
    override val onListUpdate = ObservablePropertyReference<ObservableList<E>>({ this@WrapperObservableList }, { replace(it) })
    override val onListReplace = HashSet<(ObservableList<E>) -> Unit>()
    override val onListRemove = HashSet<(E, Int) -> Unit>()

    override fun set(index: Int, element: E): E {
        val old = collection[index]
        collection[index] = element
        onListChange.invokeAll(old, element, index)
        onListUpdate.update()
        return element
    }

    override fun add(element: E): Boolean {
        val result = collection.add(element)
        val index = collection.size - 1
        if (result) {
            onListAdd.invokeAll(element, index)
            onListUpdate.update()
        }
        return result
    }

    override fun add(index: Int, element: E) {
        collection.add(index, element)
        onListAdd.invokeAll(element, index)
        onListUpdate.update()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var index = collection.size
        for (e in elements) {
            collection.add(e)
            onListAdd.invokeAll(e, index)
            index++
        }
        onListUpdate.update()
        return true
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        var currentIndex = index
        for (e in elements) {
            collection.add(currentIndex, e)
            onListAdd.invokeAll(e, currentIndex)
            currentIndex++
        }
        onListUpdate.update()
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(element: E): Boolean {
        val index = indexOf(element)
        if (index == -1) return false
        collection.removeAt(index)
        onListRemove.invokeAll(element, index)
        onListUpdate.update()
        return true
    }

    override fun removeAt(index: Int): E {
        val element = collection.removeAt(index)
        onListRemove.invokeAll(element, index)
        onListUpdate.update()
        return element
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAll(elements: Collection<E>): Boolean {
        for (element in elements) {
            val index = indexOf(element)
            if (index == -1) return false
            collection.removeAt(index)
            onListRemove.invokeAll(element, index)
        }
        onListUpdate.update()
        return true
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        collection.clear()
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }

    override fun isEmpty(): Boolean = collection.isEmpty()
    override fun contains(element: E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = collection.containsAll(elements)
    override fun listIterator(): MutableListIterator<E> = throw UnsupportedOperationException()
    override fun listIterator(index: Int): MutableListIterator<E> = throw UnsupportedOperationException()
    /**
     * WARNING:
     * This iterator MAY have issues when it removes things, because there is no way to know if the iterator is done with its work.
     * It will not call onUpdate, and calls onRemove while iterating.
     *
     * YOU HAVE BEEN WARNED.
     */
    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        val inner = collection.iterator()
        var lastIndex: Int = -1
        var lastElement: E? = null
        override fun hasNext(): Boolean = inner.hasNext()
        override fun next(): E {
            val element = inner.next()
            lastElement = element
            lastIndex++
            return element
        }

        override fun remove() {
            inner.remove()
            onListRemove.invokeAll(lastElement!!, lastIndex)
            onListUpdate.update()
            lastIndex--
        }

    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = collection.subList(fromIndex, toIndex)
    override fun get(index: Int): E = collection[index]
    override fun indexOf(element: E): Int = collection.indexOf(element)
    override fun lastIndexOf(element: E): Int = collection.lastIndexOf(element)
    override val size: Int get() = collection.size

    override fun replace(collection: Collection<E>) {
        this.collection.clear()
        this.collection.addAll(collection)
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = collection.removeAt(fromIndex)
        collection.add(toIndex, item)
        onListMove.invokeAll(item, fromIndex, toIndex)
        onListUpdate.update()
    }
}

fun <E> observableListOf(vararg items: E) = WrapperObservableList(items.toMutableList())
fun <E> MutableList<E>.observable() = WrapperObservableList(this)

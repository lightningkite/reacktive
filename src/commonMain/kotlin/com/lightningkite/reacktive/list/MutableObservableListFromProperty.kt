package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.*

class MutableObservableListFromProperty<E>(val property: MutableObservableProperty<List<E>>) : EnablingObservableList<E>(), MutableObservableList<E> {

    val collection = MutableListFromProperty<E>(property)
    var updateFromUs = false

    override fun set(index: Int, element: E): E {
        val old = collection[index]
        updateFromUs = true
        collection[index] = element
        onListChange.invokeAll(old, element, index)
        onListUpdate.update()
        return element
    }

    override fun add(element: E): Boolean {
        updateFromUs = true
        val result = collection.add(element)
        val index = collection.size - 1
        if (result) {
            onListAdd.invokeAll(element, index)
            onListUpdate.update()
        }
        return result
    }

    override fun add(index: Int, element: E) {
        updateFromUs = true
        collection.add(index, element)
        onListAdd.invokeAll(element, index)
        onListUpdate.update()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var index = collection.size
        for (e in elements) {
            updateFromUs = true
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
            updateFromUs = true
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
        updateFromUs = true
        collection.removeAt(index)
        onListRemove.invokeAll(element, index)
        onListUpdate.update()
        return true
    }

    override fun removeAt(index: Int): E {
        updateFromUs = true
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
            updateFromUs = true
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
        updateFromUs = true
        collection.clear()
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }

    override fun isEmpty(): Boolean = collection.isEmpty()
    override fun contains(element: E): Boolean = collection.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = collection.containsAll(elements)
    override fun listIterator(): MutableListIterator<E> = listIterator(0)
    override fun listIterator(index: Int): MutableListIterator<E> = object : MutableListIterator<E> {

        val inner = collection.listIterator(index)
        var cursor: Int = index
        var lastIndex: Int = -1
        var lastElement: E? = null

        override fun add(element: E) {
            updateFromUs = true
            inner.add(element)
            onListRemove.invokeAll(lastElement!!, cursor)
            cursor++
            lastIndex = -1
            onListUpdate.update()
        }

        override fun hasPrevious(): Boolean = inner.hasPrevious()

        override fun nextIndex(): Int = cursor

        override fun previous(): E {
            val element = inner.previous()
            lastElement = element
            lastIndex = cursor
            cursor--
            return element
        }

        override fun previousIndex(): Int = cursor - 1

        override fun set(element: E) {
            updateFromUs = true
            inner.set(element)
            onListChange.invokeAll(lastElement!!, element, lastIndex)
            onListUpdate.update()
        }
        override fun hasNext(): Boolean = inner.hasNext()
        override fun next(): E {
            val element = inner.next()
            lastElement = element
            lastIndex = cursor
            cursor++
            return element
        }

        override fun remove() {
            if (lastIndex == -1) throw IllegalStateException()
            updateFromUs = true
            inner.remove()
            onListRemove.invokeAll(lastElement!!, lastIndex)
            onListUpdate.update()
            cursor = lastIndex
            lastIndex = -1
        }

    }

    override fun iterator(): MutableIterator<E> = listIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = collection.subList(fromIndex, toIndex)
    override fun get(index: Int): E = collection[index]
    override fun indexOf(element: E): Int = collection.indexOf(element)
    override fun lastIndexOf(element: E): Int = collection.lastIndexOf(element)
    override val size: Int get() = collection.size

    override fun replace(collection: Collection<E>) {
        updateFromUs = true
        this.collection.clear()
        updateFromUs = true
        this.collection.addAll(collection)
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        updateFromUs = true
        val item = collection.removeAt(fromIndex)
        updateFromUs = true
        collection.add(toIndex, item)
        onListMove.invokeAll(item, fromIndex, toIndex)
        onListUpdate.update()
    }

    val listener = label@{ list: List<E> ->
        if(updateFromUs){
            updateFromUs = false
            return@label
        }
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }

    override fun enable() {
        property.add(listener)
    }

    override fun disable() {
        property.remove(listener)
    }

    override fun refresh() {
        listener(property.value)
    }
}
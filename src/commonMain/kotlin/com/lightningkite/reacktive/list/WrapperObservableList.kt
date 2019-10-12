package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.StandardEvent

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class WrapperObservableList<E>(
        val collection: MutableList<E> = mutableListOf()
) : MutableObservableList<E> {

    private val _onListAdd = StandardEvent<Pair<E, Int>>()
    override val onListAdd: Event<Pair<E, Int>> get() = _onListAdd
    private val _onListChange = StandardEvent<Triple<E, E, Int>>()
    override val onListChange: Event<Triple<E, E, Int>> get() = _onListChange
    private val _onListMove = StandardEvent<Triple<E, Int, Int>>()
    override val onListMove: Event<Triple<E, Int, Int>> get() = _onListMove
    private val _onListRemove = StandardEvent<Pair<E, Int>>()
    override val onListRemove: Event<Pair<E, Int>> get() = _onListRemove
    private val _onListReplace = StandardEvent<ObservableList<E>>()
    override val onListReplace: Event<ObservableList<E>> get() = _onListReplace

    private val _onChange = StandardEvent<ObservableList<E>>()
    override val onChange: Event<ObservableList<E>> get() = _onChange

    override fun updateAt(index: Int) {
        _onListChange {
            val value = this[index]
            Triple(value, value, index)
        }
    }

    override fun set(index: Int, element: E): E {
        val old = collection[index]
        collection[index] = element
        _onListChange { Triple(old, element, index) }
        _onChange(this)
        return element
    }

    override fun add(element: E): Boolean {
        val result = collection.add(element)
        val index = collection.size - 1
        if (result) {
            _onListAdd { element to index }
            _onChange(this)
        }
        return result
    }

    override fun add(index: Int, element: E) {
        collection.add(index, element)
        _onListAdd { element to index }
        _onChange(this)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var index = collection.size
        for (e in elements) {
            collection.add(e)
            _onListAdd { e to index }
            index++
        }
        _onChange(this)
        return true
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        var currentIndex = index
        for (e in elements) {
            collection.add(currentIndex, e)
            _onListAdd { e to currentIndex }
            currentIndex++
        }
        _onChange(this)
        return true
    }

    @Suppress("UNCHECKED_CAST")
    override fun remove(element: E): Boolean {
        val index = indexOf(element)
        if (index == -1) return false
        collection.removeAt(index)
        _onListRemove { element to index }
        _onChange(this)
        return true
    }

    override fun removeAt(index: Int): E {
        val element = collection.removeAt(index)
        _onListRemove { element to index }
        _onChange(this)
        return element
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAll(elements: Collection<E>): Boolean {
        for (element in elements) {
            val index = indexOf(element)
            if (index == -1) return false
            collection.removeAt(index)
            _onListRemove { element to index }
        }
        _onChange(this)
        return true
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        collection.clear()
        _onListReplace(this)
        _onChange(this)
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
            inner.add(element)
            _onListAdd { lastElement!! to cursor }
            cursor++
            lastIndex = -1
            _onChange(this@WrapperObservableList)
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
            inner.set(element)
            _onListChange { Triple(lastElement!!, element, lastIndex) }
            _onChange(this@WrapperObservableList)
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
            inner.remove()
            _onListRemove {
                lastElement!! to lastIndex
            }
            _onChange(this@WrapperObservableList)
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
        this.collection.clear()
        this.collection.addAll(collection)
        _onListReplace(this)
        _onChange(this)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val item = collection.removeAt(fromIndex)
        collection.add(toIndex, item)
        _onListMove { Triple(item, fromIndex, toIndex) }
        _onChange(this)
    }
}

fun <E> MutableList<E>.observable() = WrapperObservableList(this)

package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.update

class ObservableListFromProperty<E>(val property: ObservableProperty<List<E>>) : EnablingObservableList<E>() {
    override val size: Int
        get() = property.value.size

    override fun contains(element: E): Boolean = property.value.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = property.value.containsAll(elements)

    override fun get(index: Int): E = property.value.get(index)

    override fun indexOf(element: E): Int = property.value.indexOf(element)

    override fun isEmpty(): Boolean = property.value.isEmpty()

    override fun iterator(): Iterator<E> = property.value.iterator()

    override fun lastIndexOf(element: E): Int = property.value.lastIndexOf(element)

    override fun listIterator(): ListIterator<E> = property.value.listIterator()

    override fun listIterator(index: Int): ListIterator<E> = property.value.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<E> = property.value.subList(fromIndex, toIndex)

    val listener = { list: List<E> ->
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
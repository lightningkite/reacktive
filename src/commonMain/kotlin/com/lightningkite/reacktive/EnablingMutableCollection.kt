package com.lightningkite.reacktive


/**
 * A collection that calls [enable] when the collection has an element in it, and [disable] when the collection is empty.
 * Created by joseph on 12/2/16.
 */
abstract class EnablingMutableCollection<E>(val wraps:MutableList<E> = ArrayList()) : MutableList<E> by wraps {

    abstract fun enable(): Unit
    abstract fun disable(): Unit

    var active = false
    fun forceUp() {
        if (!active) {
            active = true
            enable()
        }
    }

    fun checkDown() {
        if (wraps.isEmpty() && active) {
            active = false
            disable()
        }
    }

    override fun add(element: E): Boolean {
        forceUp()
        val result = wraps.add(element)
        return result
    }

    override fun addAll(elements: Collection<E>): Boolean {
        forceUp()
        val result = wraps.addAll(elements)
        return result
    }

    override fun clear() {
        wraps.clear()
        checkDown()
    }

    override fun remove(element: E): Boolean {
        val result = wraps.remove(element)
        checkDown()
        return result
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val result = wraps.removeAll(elements)
        checkDown()
        return result
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        val result = wraps.retainAll(elements)
        checkDown()
        return result
    }
}

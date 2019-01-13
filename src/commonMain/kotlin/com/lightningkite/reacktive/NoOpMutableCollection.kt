package com.lightningkite.reacktive

@Suppress("UNCHECKED_CAST")
object NoOpMutableCollection: MutableCollection<Any?> {

    fun <T> type():MutableCollection<T> = this as MutableCollection<T>

    override val size: Int
        get() = 0

    override fun contains(element: Any?): Boolean = false
    override fun containsAll(elements: Collection<Any?>): Boolean = false
    override fun isEmpty(): Boolean = true
    override fun add(element: Any?): Boolean = false
    override fun addAll(elements: Collection<Any?>): Boolean = false
    override fun clear() {
    }
    override fun iterator(): MutableIterator<Any?> = object : MutableIterator<Any?> {
        override fun hasNext(): Boolean = false
        override fun next(): Any?  = throw UnsupportedOperationException()
        override fun remove() {}
    }
    override fun remove(element: Any?): Boolean = false
    override fun removeAll(elements: Collection<Any?>): Boolean = false
    override fun retainAll(elements: Collection<Any?>): Boolean = false
}
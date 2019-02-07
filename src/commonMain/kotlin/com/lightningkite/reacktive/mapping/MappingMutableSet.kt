package com.lightningkite.reacktive.mapping

/**
 * A list that calls functions upon changing from empty to not empty and vice versa.
 */
class MappedMutableSet<S, E>(val source: MutableSet<S>, val mapper: (S) -> E, val reverseMapper: (E) -> S) : MutableSet<E> {
    override val size: Int get() = source.size
    override fun add(element: E): Boolean = source.add(reverseMapper(element))
    override fun addAll(elements: Collection<E>): Boolean = source.addAll(elements.map(reverseMapper))
    override fun clear() = source.clear()
    override fun iterator(): MutableIterator<E> = source.iterator().mappingMutable(mapper)
    override fun remove(element: E): Boolean = source.remove(reverseMapper(element))
    override fun removeAll(elements: Collection<E>): Boolean = source.removeAll(elements.map(reverseMapper))
    override fun retainAll(elements: Collection<E>): Boolean = source.retainAll(elements.map(reverseMapper))
    override fun contains(element: E): Boolean = source.contains(reverseMapper(element))
    override fun containsAll(elements: Collection<E>): Boolean = source.containsAll(elements.map(reverseMapper))
    override fun isEmpty(): Boolean = source.isEmpty()
}

fun <S, E> MutableSet<S>.mapping(read: (S) -> E, write: (E) -> S): MutableSet<E> = MappedMutableSet(this, read, write)
fun <S, E> MutableSet<S>.mapping(write: (E) -> S): MutableSet<E> = MappedMutableSet(this, { throw IllegalStateException("Write Only") }, write)

package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.map
import com.lightningkite.reacktive.mapping.mapping
import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.property.map
import com.lightningkite.reacktive.property.transform

/**
 * Gives you a view of an observable list where the entries have been mapped.
 *
 * Created by jivie on 5/6/16.
 */
open class MappingObservableList<S, E>(
        val source: ObservableList<S>,
        val transform: (S) -> E,
        val reverse: (E) -> S
) : ObservableList<E> {
    override val size: Int get() = source.size

    override fun contains(element: E): Boolean = source.contains(reverse(element))
    override fun containsAll(elements: Collection<E>): Boolean = source.containsAll(elements.map(reverse))
    override fun get(index: Int): E = transform(source.get(index))
    override fun indexOf(element: E): Int = source.indexOf(reverse(element))
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun lastIndexOf(element: E): Int = source.lastIndexOf(reverse(element))
    override fun subList(fromIndex: Int, toIndex: Int): List<E> = source.subList(fromIndex, toIndex).map(transform).toMutableList()

    override fun listIterator(): ListIterator<E> = source.listIterator().mapping(transform)
    override fun listIterator(index: Int): ListIterator<E> = source.listIterator(index).mapping(transform)
    override fun iterator(): Iterator<E> = source.iterator().mapping(transform)

    override val onListAdd: Event<Pair<E, Int>>
        get() = source.onListAdd.map { it.first.let(transform) to it.second }
    override val onListChange: Event<Triple<E, E, Int>>
        get() = source.onListChange.map { Triple(it.first.let(transform), it.second.let(transform), it.third) }
    override val onListMove: Event<Triple<E, Int, Int>>
        get() = source.onListMove.map { Triple(it.first.let(transform), it.second, it.third) }
    override val onListRemove: Event<Pair<E, Int>>
        get() = source.onListRemove.map { it.first.let(transform) to it.second }
    override val onListReplace: Event<ObservableList<E>>
        get() = source.onListReplace.map { this }
    override val onChange: Event<ObservableList<E>>
        get() = source.onChange.map { this }
}

fun <S, E> ObservableList<S>.map(read: (S) -> E, write: (E) -> S) = MappingObservableList(this, read, write)
fun <S, E> ObservableList<S>.map(read: (S) -> E) = MappingObservableList(this, read) { throw IllegalArgumentException() }

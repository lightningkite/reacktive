package com.lightningkite.reacktive.list


import com.lightningkite.kommon.collection.mapping
import com.lightningkite.kommon.collection.mappingMutable
import com.lightningkite.kommon.collection.mappingWriteOnly
import com.lightningkite.reacktive.property.transform

/**
 * Gives you a view of an observable list where the entries have been mapped.
 *
 * Created by jivie on 5/6/16.
 */
class MappingMutableObservableList<S, E>(
        val mutableSource: MutableObservableList<S>,
        transform: (S) -> E,
        reverse: (E) -> S
) : MappingObservableList<S, E>(mutableSource, transform, reverse), MutableObservableList<E> {
    override val size: Int get() = mutableSource.size

    override fun add(element: E): Boolean = mutableSource.add(reverse(element))
    override fun add(index: Int, element: E) = mutableSource.add(index, reverse(element))
    override fun move(fromIndex: Int, toIndex: Int) = mutableSource.move(fromIndex, toIndex)
    override fun addAll(index: Int, elements: Collection<E>): Boolean = mutableSource.addAll(index, elements.map(reverse))
    override fun addAll(elements: Collection<E>): Boolean = mutableSource.addAll(elements.map(reverse))
    override fun clear() = mutableSource.clear()
    override fun remove(element: E): Boolean = mutableSource.remove(reverse(element))
    override fun removeAll(elements: Collection<E>): Boolean = mutableSource.removeAll(elements.map(reverse))
    override fun removeAt(index: Int): E = transform(mutableSource.removeAt(index))
    override fun retainAll(elements: Collection<E>): Boolean = mutableSource.retainAll(elements.map(reverse))
    override fun set(index: Int, element: E): E = transform(mutableSource.set(index, reverse(element)))

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = mutableSource.subList(fromIndex, toIndex).map(transform).toMutableList()
    override fun listIterator(): MutableListIterator<E> = mutableSource.listIterator().mapping(transform, reverse)
    override fun listIterator(index: Int): MutableListIterator<E> = mutableSource.listIterator(index).mapping(transform, reverse)
    override fun iterator(): MutableIterator<E> = mutableSource.iterator().mappingMutable(transform)
    override fun replace(collection: Collection<E>) = mutableSource.replace(collection.map(reverse))
}

fun <S, E> MutableObservableList<S>.mapping(read: (S) -> E, write: (E) -> S): MappingMutableObservableList<S, E> = MappingMutableObservableList(this, read, write)
fun <S, E> MutableObservableList<S>.mapping(read: (S) -> E): MappingMutableObservableList<S, E> = MappingMutableObservableList(this, read) { throw IllegalArgumentException() }

package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.mapping.mapping
import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.property.transform

/**
 * Gives you a view of an observable list where the entries have been mapped.
 *
 * Created by jivie on 5/6/16.
 */
open class MappingObservableList<S, E>(val source: ObservableList<S>, val transform: (S) -> E, val reverse: (E) -> S) : ObservableList<E> {
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

    val listenerMapper = { input: (E, Int) -> Unit ->
        { element: S, index: Int ->
            input(transform(element), index)
        }
    }
    override val onListAdd: MutableCollection<(E, Int) -> Unit> = source.onListAdd.mappingWriteOnly(listenerMapper)
    override val onListRemove: MutableCollection<(E, Int) -> Unit> = source.onListRemove.mappingWriteOnly(listenerMapper)
    override val onListMove: MutableCollection<(E, Int, Int) -> Unit> = source.onListMove.mappingWriteOnly { input: (E, Int, Int) -> Unit ->
        { element: S, oldIndex: Int, index: Int ->
            input(transform(element), oldIndex, index)
        }
    }
    override val onListChange: MutableCollection<(E, E, Int) -> Unit> = source.onListChange.mappingWriteOnly { input: (E, E, Int) -> Unit ->
        { old: S, element: S, index: Int ->
            input(transform(old), transform(element), index)
        }
    }

    override val onListUpdate = source.onListUpdate.transform<ObservableList<S>, ObservableList<E>> { _ -> this@MappingObservableList }
    override val onListReplace: MutableCollection<(ObservableList<E>) -> Unit> = source.onListReplace.mappingWriteOnly { input -> { input(this) } }
}

fun <S, E> ObservableList<S>.mapping(read: (S) -> E, write: (E) -> S) = MappingObservableList(this, read, write)
fun <S, E> ObservableList<S>.mapping(read: (S) -> E) = MappingObservableList(this, read) { throw IllegalArgumentException() }

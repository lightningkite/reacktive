package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.invokeAll

/**
 * An observable list of indexList that merely references another list.
 * Created by joseph on 11/2/16.
 */
abstract class IndexObservableList<E>(
        val source: ObservableList<E>
) : EnablingObservableList<E>(), ObservableList<E>, Collection<E> by source {
    val indexList = ArrayList<Int>()

    fun shift(after: Int, by: Int) {
        for (i in indexList.indices) {
            if (indexList[i] > after)
                indexList[i] += by
        }
    }

    fun shiftIncluding(after: Int, by: Int) {
        for (i in indexList.indices) {
            if (indexList[i] >= after)
                indexList[i] += by
        }
    }

    fun sourceIndex(transformedIndex: Int): Int {
        return indexList[transformedIndex]
    }

    fun transformedIndex(sourceIndex: Int): Int? {
        return indexList.indexOf(sourceIndex).takeUnless { it == -1 }
    }

    fun transformedIndexOrDefault(sourceIndex: Int, default: Int = -1): Int {
        return indexList.indexOf(sourceIndex).let {
            if (it == -1) default
            else it
        }
    }

    fun orderedTransformedIndexOrBetween(sourceIndex: Int): Int {
        return indexList.indexOfFirst { it > sourceIndex }.takeUnless { it == -1 } ?: indexList.lastIndex+1
    }

    fun addIndex(transformedIndex: Int, sourceIndex: Int) {
        indexList.add(transformedIndex, sourceIndex)
        onListAdd.invokeAll(get(transformedIndex), transformedIndex)
        onListUpdate.update()
    }

    fun removeIndex(transformedIndex: Int) {
        val old = get(transformedIndex)
        indexList.removeAt(transformedIndex)
        onListRemove.invokeAll(old, transformedIndex)
        onListUpdate.update()
    }

    fun moveIndex(oldTransformedIndex: Int, transformedIndex: Int) {
        indexList.add(transformedIndex, indexList.removeAt(oldTransformedIndex))
        onListMove.invokeAll(get(transformedIndex), oldTransformedIndex, transformedIndex)
        onListUpdate.update()
    }

    fun resetIndex(newIndicies: List<Int>) {
        indexList.clear()
        indexList.addAll(newIndicies)
        onListReplace.invokeAll(this)
        onListUpdate.update()
    }


    abstract fun onSourceListAdd(element: E, sourceIndex: Int)
    abstract fun onSourceListChange(old: E, element: E, sourceIndex: Int)
    abstract fun onSourceListMove(element: E, oldSourceIndex: Int, sourceIndex: Int)
    abstract fun onSourceListRemove(element: E, sourceIndex: Int)


    val onListAddListener: (E, Int) -> Unit = { element, index ->
        shiftIncluding(index, 1)
        onSourceListAdd(element, index)
    }
    val onListChangeListener: (E, E, Int) -> Unit = { old, element, index ->
        transformedIndex(index)?.let {
            onListChange.invokeAll(old, element, it)
            onListUpdate.update()
        }
        onSourceListChange(old, element, index)
    }
    val onListMoveListener: (E, Int, Int) -> Unit = { element, oldIndex, index ->
        val oldTransformedIndex = transformedIndex(oldIndex)
        shift(oldIndex, -1)
        shiftIncluding(index, 1)
        oldTransformedIndex?.let {
            indexList[it] = index
        }
        onSourceListMove(element, oldIndex, index)
    }
    val onListReplaceListener: (ObservableList<E>) -> Unit = {
        refresh()
    }
    val onListRemoveListener: (E, Int) -> Unit = { element, index ->
        val transformed = transformedIndex(index)
        if (transformed != null) {
            indexList.removeAt(transformed)
        }
        shift(index, -1)
        if (transformed != null) {
            onListRemove.invokeAll(element, transformed)
            onListUpdate.update()
        }
        onSourceListRemove(element, index)
    }


    override fun enable() {
        source.onListAdd.add(onListAddListener)
        source.onListChange.add(onListChangeListener)
        source.onListMove.add(onListMoveListener)
        source.onListReplace.add(onListReplaceListener)
        source.onListRemove.add(onListRemoveListener)
    }

    override fun disable() {
        source.onListAdd.remove(onListAddListener)
        source.onListChange.remove(onListChangeListener)
        source.onListMove.remove(onListMoveListener)
        source.onListReplace.remove(onListReplaceListener)
        source.onListRemove.remove(onListRemoveListener)
    }


//    override fun set(index: Int, element: E): E {
//        refreshIfNotActive()
//        source[sourceIndex(index)] = element
//        return element
//    }
//
//    override fun add(index: Int, element: E): Unit {
//        refreshIfNotActive()
//        source.add(sourceIndex(index), element)
//    }
//    override fun addAll(index: Int, elements: Collection<E>): Boolean {
//        refreshIfNotActive()
//        return source.addAll(sourceIndex(index), elements)
//    }
//
//    override fun move(fromIndex: Int, toIndex: Int) {
//        refreshIfNotActive()
//        source.move(sourceIndex(fromIndex), sourceIndex(toIndex))
//    }
//
//    override fun removeAt(index: Int): E {
//        refreshIfNotActive()
//        return source.removeAt(sourceIndex(index))
//    }
//
//    override fun replace(collection: Collection<E>) = source.replace(collection)

    override fun isEmpty(): Boolean {
        refreshIfNotActive()
        return indexList.isEmpty()
    }

    override fun contains(element: E): Boolean {
        refreshIfNotActive()
        return indexList.contains(source.indexOf(element))
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        refreshIfNotActive()
        return indexList.containsAll(elements.map { source.indexOf(it) })
    }

    override fun listIterator(index: Int): MutableListIterator<E> = object : MutableListIterator<E> {
        init {
            refreshIfNotActive()
        }

        val inner = indexList.listIterator(index)
        override fun hasPrevious(): Boolean = inner.hasPrevious()
        override fun nextIndex(): Int = inner.nextIndex()
        override fun previous(): E = source[inner.previous()]
        override fun previousIndex(): Int = inner.previousIndex()
        override fun hasNext(): Boolean = inner.hasNext()
        override fun next(): E = source[inner.next()]

        override fun add(element: E) = throw UnsupportedOperationException()
        override fun remove() = inner.remove()
        override fun set(element: E) = throw UnsupportedOperationException()
    }

    override fun listIterator(): MutableListIterator<E> = listIterator(0)
    override fun iterator(): MutableIterator<E> = listIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = throw UnsupportedOperationException()
    override fun get(index: Int): E {
        refreshIfNotActive()
        return source[sourceIndex(index)]
    }

    override fun indexOf(element: E): Int {
        refreshIfNotActive()
        return indexList.indexOf(source.indexOf(element))
    }

    override fun lastIndexOf(element: E): Int {
        refreshIfNotActive()
        return indexList.lastIndexOf(source.lastIndexOf(element))
    }

    override val size: Int
        get() {
            refreshIfNotActive()
            return indexList.size
        }

}

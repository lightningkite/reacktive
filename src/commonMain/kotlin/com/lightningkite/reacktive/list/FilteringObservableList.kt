package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.list.asMutable

/**
 * Allows you to observe the changes to a list.
 * Created by josep on 9/7/2015.
 */
class FilteringObservableList<E>(
        val mutableSource: MutableObservableList<E>,
        filter: (E) -> Boolean = { true }
) : IndexObservableList<E>(mutableSource), MutableObservableList<E> {

    override fun set(index: Int, element: E): E {
        refreshIfNotActive()
        mutableSource[sourceIndex(index)] = element
        return element
    }
    override fun add(index: Int, element: E): Unit {
        refreshIfNotActive()
        mutableSource.add(sourceIndex(index), element)
    }
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        refreshIfNotActive()
        return mutableSource.addAll(sourceIndex(index), elements)
    }
    override fun move(fromIndex: Int, toIndex: Int) {
        refreshIfNotActive()
        mutableSource.move(sourceIndex(fromIndex), sourceIndex(toIndex))
    }
    override fun removeAt(index: Int): E {
        refreshIfNotActive()
        return mutableSource.removeAt(sourceIndex(index))
    }
    override fun replace(collection: Collection<E>) = mutableSource.replace(collection)
    override fun add(element: E): Boolean = if(filter(element)){
        mutableSource.add(element)
        true
    } else false
    override fun addAll(elements: Collection<E>): Boolean = mutableSource.addAll(elements.filter(filter))
    override fun clear() = mutableSource.clear()
    override fun remove(element: E): Boolean = mutableSource.remove(element)
    override fun removeAll(elements: Collection<E>): Boolean = mutableSource.removeAll(elements)
    override fun retainAll(elements: Collection<E>): Boolean = mutableSource.retainAll(elements)


    override fun refresh() {
        resetIndex(source.indices.filter { filter(source[it]) })
    }

    var filter: (E) -> Boolean = filter
        set(value) {
            field = value
            refresh()
        }

    init {
        refresh()
    }

    override fun onSourceListAdd(element: E, sourceIndex: Int) {
        if (filter(element)) {
            addIndex(orderedTransformedIndexOrBetween(sourceIndex), sourceIndex)
        }
    }

    override fun onSourceListChange(old: E, element: E, sourceIndex: Int) {
        val oldPassed = sourceIndex in indexList
        val passes = filter(element)
        if (oldPassed && !passes) {
            removeIndex(transformedIndexOrDefault(sourceIndex, -1))
        }
        if (passes && !oldPassed) {
            addIndex(orderedTransformedIndexOrBetween(sourceIndex), sourceIndex)
        }
    }

    override fun onSourceListMove(element: E, oldSourceIndex: Int, sourceIndex: Int) {
        transformedIndex(sourceIndex)?.let {
            moveIndex(
                    it,
                    orderedTransformedIndexOrBetween(sourceIndex).let{ new ->
                        if(new > it) new - 1
                        else new
                    }
            )
        }
    }

    override fun onSourceListRemove(element: E, sourceIndex: Int) {
        /*Handled by IndexObservableList*/
    }
}

fun <E> MutableObservableList<E>.filtering(): FilteringObservableList<E> = FilteringObservableList(this)
fun <E> MutableObservableList<E>.filtering(initFilter: (E) -> Boolean): FilteringObservableList<E> = FilteringObservableList(this, initFilter).apply {
    filter = initFilter
}

fun <E> ObservableList<E>.filtering(): FilteringObservableList<E> = FilteringObservableList(this.asMutable())
fun <E> ObservableList<E>.filtering(initFilter: (E) -> Boolean): FilteringObservableList<E> = FilteringObservableList(this.asMutable(), initFilter).apply {
    filter = initFilter
}

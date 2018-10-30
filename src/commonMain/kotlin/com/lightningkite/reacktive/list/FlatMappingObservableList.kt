package com.lightningkite.reacktive.list


import com.lightningkite.reacktive.invokeAll

/**
 * Gives you a flat-mapped view of an observable list.
 * Created by jivie on 5/6/16.
 */
class FlatMappingObservableList<S, E>(
        val source: MutableObservableList<S>,
        val mapper: (S) -> MutableObservableList<E>
) : MutableObservableList<E>, EnablingObservableList<E>() {

    val boundaryIndexes = ArrayList<Int>()

    fun getIndex(pair: Pair<Int, Int>): Int {
        return boundaryIndexes[pair.first] + pair.second
    }

    fun getIndicies(index: Int): Pair<Int, Int> {
        for (outerIndex in 0..boundaryIndexes.lastIndex - 1) {
            val position = boundaryIndexes[outerIndex + 1]
            if (index < position) return outerIndex to index - boundaryIndexes[outerIndex]
        }
        throw IndexOutOfBoundsException()
    }

    fun modifyIndiciesAfter(listIndex: Int, by: Int) {
        for (i in listIndex + 1..boundaryIndexes.lastIndex) {
            boundaryIndexes[i] += by
        }
    }

    fun insertBoundaryIndex(overallIndex: Int, size: Int): Int {
        for (i in overallIndex..boundaryIndexes.lastIndex) {
            boundaryIndexes[i] += size
        }
        val newStart = boundaryIndexes[overallIndex] - size
        boundaryIndexes.add(overallIndex, newStart)
        return newStart
    }

    fun removeBoundaryIndex(overallIndex: Int, item: S): Int {
        val oldBoundary = boundaryIndexes[overallIndex]
        val size = item.let(mapper).size
        for (i in overallIndex..boundaryIndexes.lastIndex) {
            boundaryIndexes[i] -= size
        }
        boundaryIndexes.removeAt(overallIndex)
        return oldBoundary
    }

    override val size: Int
        get() {
            refreshIfNotActive()
            return boundaryIndexes.last()
        }

    override fun contains(element: E): Boolean = source.any { mapper(it).contains(element) }
    override fun containsAll(elements: Collection<E>): Boolean = source.flatMap(mapper).containsAll(elements)
    override fun get(index: Int): E {
        refreshIfNotActive()
        val i = getIndicies(index)
        return source[i.first].let(mapper)[i.second]
    }

    override fun indexOf(element: E): Int {
        refreshIfNotActive()
        return source.flatMap(mapper).indexOf(element)
    } //TODO - could be more efficient

    override fun isEmpty(): Boolean = source.isEmpty() || source.all { mapper(it).isEmpty() }
    override fun lastIndexOf(element: E): Int {
        refreshIfNotActive()
        return source.flatMap(mapper).lastIndexOf(element)
    } //TODO - could be more efficient

    override fun add(element: E): Boolean {
        refreshIfNotActive()
        return source.last().let(mapper).add(element)
    }

    override fun add(index: Int, element: E) {
        refreshIfNotActive()
        val i = getIndicies(index)
        source[i.first].let(mapper).add(i.second, element)
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        refreshIfNotActive()
        val from = getIndicies(fromIndex)
        val to = getIndicies(if (toIndex > fromIndex) toIndex - 1 else toIndex)
        //TODO: Add/remove?
        if (from.first != to.first) throw UnsupportedOperationException("You can't move an item between lists.")
        source[from.first].let(mapper).move(from.second, to.second)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        refreshIfNotActive()
        return source.last().let(mapper).addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        refreshIfNotActive()
        val i = getIndicies(index)
        return source[i.first].let(mapper).addAll(i.second, elements)
    }

    override fun clear() = source.clear()
    override fun remove(element: E): Boolean {
        refreshIfNotActive()
        return source.any { it.let(mapper).remove(element) }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        refreshIfNotActive()
        source.forEach { it.let(mapper).removeAll(elements) }
        return true
    }

    override fun removeAt(index: Int): E {
        refreshIfNotActive()
        val i = getIndicies(index)
        return source[i.first].let(mapper).removeAt(i.second)
    }

    override fun retainAll(elements: Collection<E>): Boolean = throw UnsupportedOperationException()
    override fun set(index: Int, element: E): E {
        refreshIfNotActive()
        val i = getIndicies(index)
        source[i.first].let(mapper)[i.second] = element
        return element
    }

    override fun replace(collection: Collection<E>) = throw UnsupportedOperationException()
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = throw UnsupportedOperationException()

    //TODO: Both of these can be supported.
    override fun listIterator(): MutableListIterator<E> = throw UnsupportedOperationException()

    override fun listIterator(index: Int): MutableListIterator<E> = throw UnsupportedOperationException()

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        init {
            refreshIfNotActive()
        }

        val sourceIterator = source.iterator()
        var subIterator = if (sourceIterator.hasNext()) sourceIterator.next().let(mapper).iterator() else null
        override fun hasNext(): Boolean {
            return if (subIterator == null) false
            else if (subIterator!!.hasNext()) true
            else if (sourceIterator.hasNext()) {
                subIterator = sourceIterator.next().let(mapper).iterator()
                hasNext()
            } else false
        }

        override fun next(): E {
            if (!hasNext()) throw IllegalStateException("No more items")
            return subIterator!!.next()
        }

        override fun remove() {
            subIterator!!.remove()
        }
    }

    fun onTotalItemAdd(item: S, index: Int) {
        val list = item.let(mapper)
        val newBoundary = insertBoundaryIndex(index, list.size)
        for (i in 0..list.size - 1) {
            onListAdd.invokeAll(list[i], newBoundary + i)
        }
        onListUpdate.update()
    }

    fun onTotalItemRemove(item: S, index: Int) {
        val list = item.let(mapper)
        val oldBoundary = removeBoundaryIndex(index, item)
        for (i in list.size - 1 downTo 0) {
            if (try {
                        list[list.size - 1]; true
                    } catch (e: Exception) {
                        false
                    })
                println("Get error at $i")
            onListRemove.invokeAll(list[i], oldBoundary + i)
        }
        onListUpdate.update()
    }

    fun clearOldListeners() {
        for ((list, set) in listenerSets) {
            list.removeListenerSet(set)
        }
    }

    val listenerSets = HashMap<ObservableList<E>, ObservableListListenerSet<E>>()
    val overallListenerSet: ObservableListListenerSet<S> = ObservableListListenerSet<S>(
            onAddListener = { item, index ->
                onTotalItemAdd(item, index)

                val newSet = subListenerSet(item)
                val list = item.let(mapper)
                list.addListenerSet(newSet)
                listenerSets[list] = newSet
            },
            onRemoveListener = { item, index ->
                onTotalItemRemove(item, index)

                val list: ObservableList<E> = item.let(mapper)
                val set = listenerSets[list]
                if (set != null) {
                    list.removeListenerSet(set)
                }
            },
            onMoveListener = { item, oldIndex, index ->
                //TODO could move each item
                onTotalItemRemove(item, oldIndex)
                onTotalItemAdd(item, index)
            },
            onChangeListener = { old, item, index ->
                //TODO could change ones that are there then remove/add until fully used
                onTotalItemRemove(old, index)
                onTotalItemAdd(item, index)
            },
            onReplaceListener = { _ ->
                refresh()
            }
    )

    fun subListenerSet(itemContainingList: S) = ObservableListListenerSet<E>(
            onAddListener = { item, index ->
                val myIndex = source.indexOf(itemContainingList)
                if (myIndex == -1) throw IllegalStateException()
                val fullIndex = getIndex(myIndex to index)
                modifyIndiciesAfter(myIndex, 1)
                onListAdd.invokeAll(item, fullIndex)
                onListUpdate.update()
            },
            onRemoveListener = { item, index ->
                val myIndex = source.indexOf(itemContainingList)
                if (myIndex == -1) throw IllegalStateException()
                val fullIndex = getIndex(myIndex to index)
                modifyIndiciesAfter(myIndex, -1)
                onListRemove.invokeAll(item, fullIndex)
                onListUpdate.update()
            },
            onMoveListener = { item, oldIndex, index ->
                val myIndex = source.indexOf(itemContainingList)
                val oldTotalIndex = getIndex(myIndex to oldIndex)
                val newTotalIndex = getIndex(myIndex to index)
                onListMove.invokeAll(item, oldTotalIndex, newTotalIndex)
                onListUpdate.update()
            },
            onChangeListener = { old, item, index ->
                val myIndex = source.indexOf(itemContainingList)
                if (myIndex == -1) throw IllegalStateException()
                val fullIndex = getIndex(myIndex to index)
                onListChange.invokeAll(old, item, fullIndex)
                onListUpdate.update()
            },
            onReplaceListener = { _ ->
                val myIndex = source.indexOf(itemContainingList)
                if (myIndex == -1) throw IllegalStateException()
                onTotalItemRemove(itemContainingList, myIndex)
                onTotalItemAdd(itemContainingList, myIndex)
            }
    )

    var connected = false
    override fun enable() {
        if (connected) return
        for (item in source) {
            val newSet = subListenerSet(item)
            val list = item.let(mapper)
            list.addListenerSet(newSet)
            listenerSets[list] = newSet
        }
        source.addListenerSet(overallListenerSet)
        connected = true
    }

    override fun disable() {
        if (!connected) return
        source.removeListenerSet(overallListenerSet)
        clearOldListeners()
        connected = false
    }

    override fun refresh() {
        boundaryIndexes.clear()
        var current = 0
        boundaryIndexes.add(current)
        for (s in source) {
            current += s.let(mapper).size
            boundaryIndexes.add(current)
        }
    }
}

fun <S, E> MutableObservableList<S>.flatMapping(mapper: (S) -> MutableObservableList<E>): FlatMappingObservableList<S, E> = FlatMappingObservableList(this, mapper)
fun <S, E> ObservableList<S>.flatMapping(mapper: (S) -> ObservableList<E>): FlatMappingObservableList<S, E> = FlatMappingObservableList(this.asMutable()) { mapper.invoke(it).asMutable() }

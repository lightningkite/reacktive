package com.lightningkite.reacktive.collection

import com.lightningkite.kommon.collection.addSorted
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.list.EnablingObservableList
import com.lightningkite.reacktive.list.MutableObservableList

class SortingObservableList<V>(
        val source: MutableObservableCollection<V>,
        val comparator: Comparator<V>
) : EnablingObservableList<V>(), MutableObservableCollection<V> {
    override val size: Int
        get() = source.size

    override fun contains(element: V): Boolean = source.contains(element)
    override fun change(old: V, new: V) = source.change(old, new)
    override fun containsAll(elements: Collection<V>): Boolean = source.containsAll(elements)
    override fun isEmpty(): Boolean = source.isEmpty()
    override fun replace(collection: Collection<V>) = source.replace(collection)
    override fun add(element: V): Boolean = source.add(element)
    override fun addAll(elements: Collection<V>): Boolean = source.addAll(elements)
    override fun clear() = source.clear()
    override fun remove(element: V): Boolean = source.remove(element)
    override fun removeAll(elements: Collection<V>): Boolean = source.removeAll(elements)
    override fun retainAll(elements: Collection<V>): Boolean = source.retainAll(elements)

    val sortedCopy = ArrayList<V>()
    override fun refresh() {
        sortedCopy.clear()
        sortedCopy.addAll(source.sortedWith(comparator))
    }

    val onCollectionAddListener = { new: V ->
        onListAdd.invokeAll(new, sortedCopy.addSorted(new, comparator))
    }
    val onCollectionChangeListener = { old: V, new: V ->
        val oldIndex = sortedCopy.indexOf(old)
        sortedCopy.removeAt(oldIndex)
        onListRemove.invokeAll(old, oldIndex)
        val newIndex = sortedCopy.addSorted(new, comparator)
        onListAdd.invokeAll(new, newIndex)
    }
    var supressRemoveListener = false
    val onCollectionRemoveListener = removeL@{ old: V ->
        if(supressRemoveListener){
            supressRemoveListener = false
            return@removeL
        }
        val oldIndex = sortedCopy.indexOf(old)
        sortedCopy.removeAt(oldIndex)
        onListRemove.invokeAll(old, oldIndex)
    }
    val onCollectionReplaceListener = { collection: ObservableCollection<V> ->
        refresh()
        onListReplace.invokeAll(this)
    }

    val onCollectionUpdateListener = { collection: ObservableCollection<V> ->
        onListUpdate.invokeAll(this)
    }

    override fun enable() {
        source.onCollectionAdd += onCollectionAddListener
        source.onCollectionChange += onCollectionChangeListener
        source.onCollectionRemove += onCollectionRemoveListener
        source.onCollectionReplace += onCollectionReplaceListener
        source.onCollectionUpdate += onCollectionUpdateListener
    }

    override fun disable() {
        source.onCollectionAdd -= onCollectionAddListener
        source.onCollectionChange -= onCollectionChangeListener
        source.onCollectionRemove -= onCollectionRemoveListener
        source.onCollectionReplace -= onCollectionReplaceListener
        source.onCollectionUpdate -= onCollectionUpdateListener
    }

    override fun get(index: Int): V {
        refreshIfNotActive()
        return sortedCopy[index]
    }

    override fun indexOf(element: V): Int {
        refreshIfNotActive()
        return sortedCopy.indexOf(element)
    }

    override fun lastIndexOf(element: V): Int {
        refreshIfNotActive()
        return sortedCopy.lastIndexOf(element)
    }

    override fun iterator(): MutableIterator<V>  = object : MutableIterator<V> {
        init{
            refreshIfNotActive()
        }

        var last:V? = null
        var lastIndex = 0
        val underlying = sortedCopy.iterator()

        override fun hasNext(): Boolean = underlying.hasNext()

        override fun next(): V {
            lastIndex++
            val result = underlying.next()
            last = result
            return result
        }

        override fun remove() {
            supressRemoveListener = true
            underlying.remove()
            @Suppress("UNCHECKED_CAST")
            onListRemove.invokeAll(last as V, lastIndex)
        }
    }

    override fun listIterator(): ListIterator<V> = listIterator(0)
    override fun listIterator(index: Int): ListIterator<V>  = object : ListIterator<V> {
        init{
            refreshIfNotActive()
        }

        var last:V? = null
        var lastIndex = 0
        val underlying = sortedCopy.listIterator(index)

        override fun hasNext(): Boolean = underlying.hasNext()

        override fun next(): V {
            lastIndex++
            val result = underlying.next()
            last = result
            return result
        }

        override fun hasPrevious(): Boolean = underlying.hasPrevious()

        override fun nextIndex(): Int = underlying.nextIndex()

        override fun previous(): V {
            lastIndex--
            val result = underlying.previous()
            last = result
            return result
        }

        override fun previousIndex(): Int = underlying.previousIndex()
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<V> {
        refreshIfNotActive()
        return sortedCopy.subList(fromIndex, toIndex)
    }
}
fun <E: Comparable<E>> MutableObservableCollection<E>.sorting() = SortingObservableList(this, compareBy { it })
fun <E: Comparable<E>> MutableObservableCollection<E>.sortingDescending() = SortingObservableList(this, compareByDescending { it })
fun <E> MutableObservableCollection<E>.sorting(by: Comparator<E>) = SortingObservableList(this, by)
fun <E> MutableObservableCollection<E>.asList() = this as? MutableObservableList<E> ?: SortingObservableList(this, compareBy { it?.hashCode() ?: Int.MAX_VALUE })
fun <E: Comparable<E>> ObservableCollection<E>.sorting() = SortingObservableList(this.asMutable(), compareBy { it })
fun <E: Comparable<E>> ObservableCollection<E>.sortingDescending() = SortingObservableList(this.asMutable(), compareByDescending { it })
fun <E> ObservableCollection<E>.sorting(by: Comparator<E>) = SortingObservableList(this.asMutable(), by)
fun <E> ObservableCollection<E>.asList() = this as? MutableObservableList<E> ?: SortingObservableList(this.asMutable(), compareBy { it?.hashCode() ?: Int.MAX_VALUE })

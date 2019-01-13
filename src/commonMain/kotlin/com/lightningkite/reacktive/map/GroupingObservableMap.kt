package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.EnablingObject
import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.invokeAll
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.ReferenceObservableProperty
import com.lightningkite.reacktive.property.update

class GroupingObservableMap<K, E>(
        val source: ObservableCollection<E>,
        val getKey: (E) -> K
) : ObservableMap<K, InnerCollection<E>>, EnablingObject() {

    inner class OnUpdateClass : ObservableProperty<ObservableMap<K, InnerCollection<E>>>, MutableCollection<(ObservableMap<K, InnerCollection<E>>) -> Unit> by this.SubEnablingCollection() {
        override val value: ObservableMap<K, InnerCollection<E>>
            get() = this@GroupingObservableMap

        fun update() {
            for (item in this) {
                item.invoke(this@GroupingObservableMap)
            }
        }
    }

    override val onMapPut: MutableCollection<(key: K, hadPrevious: Boolean, previous: InnerCollection<E>?, new: InnerCollection<E>) -> Unit> = this.SubEnablingCollection()
    override val onMapRemove: MutableCollection<(key: K, value: InnerCollection<E>) -> Unit> = this.SubEnablingCollection()
    override val onMapUpdate: OnUpdateClass = OnUpdateClass()
    override val onMapReplace: MutableCollection<(ObservableMap<K, InnerCollection<E>>) -> Unit> = this.SubEnablingCollection()

    override val entries = EntryObservableSet(
            parent = this,
            nonObservableEntryIterator = {underlying.entries.iterator()}
    )
    override val keys = KeyObservableSet(
            parent = this,
            nonObservableEntryIterator = {underlying.entries.iterator()}
    )
    override val values = ValueObservableSet(
            parent = this,
            nonObservableEntryIterator = {underlying.entries.iterator()}
    )

    val underlying = HashMap<K, InnerCollection<E>>()

    override val size: Int
        get() {
            this.refreshIfNotActive()
            return underlying.size
        }

    override fun containsKey(key: K): Boolean {
        this.refreshIfNotActive()
        return underlying.containsKey(key)
    }

    override fun containsValue(value: InnerCollection<E>): Boolean {
        this.refreshIfNotActive()
        return underlying.containsValue(value)
    }

    override fun get(key: K): InnerCollection<E>? {
        this.refreshIfNotActive()
        return underlying.get(key)
    }

    override fun isEmpty(): Boolean {
        this.refreshIfNotActive()
        return underlying.isEmpty()
    }

    private fun grab(key: K) = underlying[key] ?: run {
        val new = InnerCollection(this, ArrayList<E>())
        underlying[key] = new
        onMapPut.invokeAll(key, false, null, new)
        onMapUpdate.update()
        new
    }


    val onCollectionAddListener: (value: E) -> Unit = {
        grab(it.let(getKey)).add(it)
    }
    val onCollectionChangeListener: (old: E, new: E) -> Unit = { old, new ->
        var oldKey = old.let(getKey)
        if(old !in grab(oldKey)){
            //Find where it actually is.
            //This is necessary to handle mutations that aren't part of the event system.
            oldKey = underlying.entries.first { old in it.value }.key
        }
        val newKey = new.let(getKey)
        if (oldKey == newKey) {
            grab(newKey).change(old, new)
        } else {
            if(!grab(oldKey).remove(old)) throw IllegalStateException()
            grab(newKey).add(new)
        }
    }
    val onCollectionRemoveListener: (value: E) -> Unit = { value ->
        val key = value.let(getKey)
        grab(key).let {
            if(!it.remove(value)) throw IllegalStateException()
            if (it.isEmpty()) {
                underlying.remove(key)
                onMapRemove.invokeAll(key, it)
                onMapUpdate.update()
            }
        }
    }
    val onCollectionReplaceListener: (ObservableCollection<E>) -> Unit = {}

    override fun enable() {
        source.onCollectionAdd.add(onCollectionAddListener)
        source.onCollectionChange.add(onCollectionChangeListener)
        source.onCollectionRemove.add(onCollectionRemoveListener)
        source.onCollectionReplace.add(onCollectionReplaceListener)
    }

    override fun disable() {
        source.onCollectionAdd.remove(onCollectionAddListener)
        source.onCollectionChange.remove(onCollectionChangeListener)
        source.onCollectionRemove.remove(onCollectionRemoveListener)
        source.onCollectionReplace.remove(onCollectionReplaceListener)
    }

    override fun refresh() {
        val grouped = source.groupBy(getKey)
        for ((key, collection) in grouped) {
            grab(key).replace(collection)
        }
        for (key in underlying.keys) {
            underlying[key]?.let {
                if (it.isEmpty()) {
                    underlying.remove(key)
                }
            }
        }
        onMapReplace.invokeAll(this)
        onMapUpdate.update()
    }
}

fun <K, E> ObservableCollection<E>.groupingBy(
        getKey: (E) -> K
) = GroupingObservableMap(this, getKey)


class InnerCollection<E>(val enablingObject: EnablingObject, val wraps: MutableCollection<E>) : MutableObservableCollection<E> {
    override val size: Int get() = wraps.size
    override fun contains(element: E): Boolean = wraps.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = wraps.containsAll(elements)
    override fun isEmpty(): Boolean = wraps.isEmpty()
    override fun add(element: E): Boolean {
        return if (wraps.add(element)) {
            onCollectionAdd.invokeAll(element)
            onCollectionUpdate.update()
            true
        } else {
            false
        }
    }

    override fun change(old: E, new: E) {
        if(!wraps.remove(old)) throw IllegalStateException("Change attempted, but $old was not in the collection.")
        wraps.add(new)
        onCollectionChange.invokeAll(old, new)
        onCollectionUpdate.update()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var added = false
        for (element in elements) {
            added = added || add(element)
        }
        return added
    }

    override fun clear() {
        wraps.clear()
        onCollectionReplace.invokeAll(this)
        onCollectionUpdate.update()
    }

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {

        val underlying = wraps.iterator()
        var last: E? = null

        override fun hasNext(): Boolean = underlying.hasNext()

        override fun next(): E {
            val n = underlying.next()
            last = n
            return n
        }

        override fun remove() {
            underlying.remove()
            @Suppress("UNCHECKED_CAST")
            onCollectionRemove.invokeAll(last as E)
            onCollectionUpdate.update()
        }
    }

    override fun remove(element: E): Boolean {
        return if (wraps.remove(element)) {
            onCollectionRemove.invokeAll(element)
            onCollectionUpdate.update()
            true
        } else {
            false
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        var removed = false
        for (element in elements) {
            removed = removed || remove(element)
        }
        return removed
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return if (wraps.retainAll(elements)) {
            onCollectionReplace.invokeAll(this)
            onCollectionUpdate.update()
            true
        } else {
            false
        }
    }

    override fun replace(collection: Collection<E>) {
        wraps.clear()
        wraps.addAll(collection)
        onCollectionReplace.invokeAll(this)
        onCollectionUpdate.update()
    }

    override val onCollectionAdd: MutableCollection<(value: E) -> Unit> = enablingObject.SubEnablingCollection()
    override val onCollectionChange: MutableCollection<(old: E, new: E) -> Unit> = enablingObject.SubEnablingCollection()
    override val onCollectionRemove: MutableCollection<(value: E) -> Unit> = enablingObject.SubEnablingCollection()
    override val onCollectionUpdate = ReferenceObservableProperty<ObservableCollection<E>>({ this@InnerCollection }, { replace(it) })
    override val onCollectionReplace: MutableCollection<(ObservableCollection<E>) -> Unit> = enablingObject.SubEnablingCollection()
}

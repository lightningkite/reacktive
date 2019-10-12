package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.combine
import com.lightningkite.reacktive.event.filter
import com.lightningkite.reacktive.event.map
import com.lightningkite.reacktive.property.ObservableProperty

fun <E> ObservableList<E>.firstOrNullObservable(index: Int = 0) = object: ObservableProperty<E?> {
    override val onChange: Event<E?> = combine(
            this@firstOrNullObservable.onListAdd.filter { it.second == index }.map { it.first },
            this@firstOrNullObservable.onListRemove.filter { it.second == index }.map { this@firstOrNullObservable.lastOrNull() },
            this@firstOrNullObservable.onListChange.filter { it.third == index }.map { it.second },
            this@firstOrNullObservable.onListReplace.map { this@firstOrNullObservable.getOrNull(index) }
    )
    override val value: E?
        get() = this@firstOrNullObservable.getOrNull(index)
}

fun <E> ObservableList<E>.lastOrNullObservable(index: Int = 0) = object: ObservableProperty<E?> {
    override val onChange: Event<E?> = combine(
            this@lastOrNullObservable.onListAdd.filter { it.second == this@lastOrNullObservable.lastIndex - index }.map { it.first },
            this@lastOrNullObservable.onListRemove.filter { it.second == this@lastOrNullObservable.size - index }.map { this@lastOrNullObservable.lastOrNull() },
            this@lastOrNullObservable.onListChange.filter { it.third == this@lastOrNullObservable.lastIndex - index }.map { it.second },
            this@lastOrNullObservable.onListReplace.map { this@lastOrNullObservable.getOrNull(this@lastOrNullObservable.lastIndex - index) }
    )
    override val value: E?
        get() = this@lastOrNullObservable.getOrNull(this@lastOrNullObservable.lastIndex - index)
}

fun <E> ObservableList<E>.asProperty() = object : ObservableProperty<ObservableList<E>> {
    override val onChange: Event<ObservableList<E>>
        get() = this@asProperty.onChange
    override val value: ObservableList<E>
        get() = this@asProperty
}
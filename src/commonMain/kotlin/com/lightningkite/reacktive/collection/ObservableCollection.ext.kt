package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.property.ObservableProperty


fun <E> ObservableCollection<E>.asProperty() = object : ObservableProperty<ObservableCollection<E>> {
    override val onChange: Event<ObservableCollection<E>>
        get() = this@asProperty.onChange
    override val value: ObservableCollection<E>
        get() = this@asProperty
}
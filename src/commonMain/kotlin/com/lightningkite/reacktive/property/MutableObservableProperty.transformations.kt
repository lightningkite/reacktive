package com.lightningkite.reacktive.property

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.event.Event
import com.lightningkite.reacktive.event.map
import kotlin.jvm.JvmName

@Deprecated("Use `map` instead", ReplaceWith("this.map(transform, reverse)", "com.lightningkite.reacktive.property.map"))
fun <A, B> MutableObservableProperty<A>.transform(transform:(A)->B, reverse: (B)->A): MutableObservableProperty<B> = map(transform, reverse)
fun <A, B> MutableObservableProperty<A>.map(transform:(A)->B, reverse: (B)->A): MutableObservableProperty<B> = object : MutableObservableProperty<B> {
    override val onChange: Event<B>
        get() = this@map.onChange.map(transform)
    override var value: B
        get() = transform(this@map.value)
        set(value) {
            this@map.value = reverse(value)
        }
}

@JvmName("flatMapMutable")
fun <A, B> ObservableProperty<A>.flatMap(transform: (A)->MutableObservableProperty<B>) = object : MutableObservableProperty<B> {
    override val onChange: Event<B> = flatMapOnChange(transform)
    override var value: B
        get() = this@flatMap.value.let(transform).value
        set(value){
            this@flatMap.value.let(transform).value = value
        }
}
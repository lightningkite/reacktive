package com.lightningkite.reacktive.property

import com.lightningkite.kommon.Closeable
import com.lightningkite.kommon.CloseableLambda
import com.lightningkite.reacktive.event.*
import kotlin.jvm.JvmName
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0

fun <T> KMutableProperty0<T>.observableProperty() = object: MutableObservableProperty<T> {
    override val onChange = StandardEvent<T>()
    override var value: T
        get() = this@observableProperty.get()
        set(value) {
            this@observableProperty.set(value)
            onChange.invokeAll(value)
        }
}

fun <R, T> KMutableProperty1<R, T>.observableProperty(receiver: R) = object: MutableObservableProperty<T> {
    override val onChange = StandardEvent<T>()
    override var value: T
        get() = this@observableProperty.get(receiver)
        set(value) {
            this@observableProperty.set(receiver, value)
            onChange.invokeAll(value)
        }
}

@Deprecated("Use `map` instead", ReplaceWith("this.map(transform)", "com.lightningkite.reacktive.property.map"))
fun <A, B> ObservableProperty<A>.transform(transform: (A) -> B): ObservableProperty<B> = map(transform)

fun <A, B> ObservableProperty<A>.map(transform: (A) -> B): ObservableProperty<B> = object : ObservableProperty<B> {
    override val onChange: Event<B>
        get() = this@map.onChange.map(transform)
    override val value: B
        get() = transform(this@map.value)
}

fun <A, B, Z> combine(a: ObservableProperty<A>, b: ObservableProperty<B>, transform: (A, B) -> Z): ObservableProperty<Z> = object : ObservableProperty<Z> {
    override val onChange: Event<Z> = combine(
            a = a.onChange,
            fromA = { transform(it, b.value) },
            b = b.onChange,
            fromB = { transform(a.value, it) }
    )
    override val value: Z get() = transform(a.value, b.value)
}

fun <A, B, C, Z> combine(a: ObservableProperty<A>, b: ObservableProperty<B>, c: ObservableProperty<C>, transform: (A, B, C) -> Z): ObservableProperty<Z> = object : ObservableProperty<Z> {
    override val onChange: Event<Z> = combine(
            a = a.onChange,
            fromA = { transform(it, b.value, c.value) },
            b = b.onChange,
            fromB = { transform(a.value, it, c.value) },
            c = c.onChange,
            fromC = { transform(a.value, b.value, it) }
    )
    override val value: Z get() = transform(a.value, b.value, c.value)
}

fun <T> combine(vararg properties: ObservableProperty<*>, transform: ()->T): ObservableProperty<T> = object: ObservableProperty<T> {
    override val onChange: Event<T>
        get() = combineUntyped(*properties.map { it.onChange }.toTypedArray()).map { transform() }
    override val value: T
        get() = transform()
}

@JvmName("flatMapEvent")
fun <A, B> ObservableProperty<A>.flatMap(transform: (A) -> Event<B>) = object : Event<B> {
    override fun listen(listener: (B) -> Unit): Closeable {
        var current: Closeable = this@flatMap.value.let(transform).listen(listener)
        val closeA = this@flatMap.onChange.listen {
            current.close()
            val new = this@flatMap.value.let(transform)
            current = new.listen(listener)
        }
        return CloseableLambda {
            current.close()
            closeA.close()
        }
    }
}

fun <A, B> ObservableProperty<A>.flatMapOnChange(transform: (A) -> ObservableProperty<B>) = object : Event<B> {
    override fun listen(listener: (B) -> Unit): Closeable {
        var current: Closeable = this@flatMapOnChange.value.let(transform).onChange.listen(listener)
        val closeA = this@flatMapOnChange.onChange.listen {
            current.close()
            val new = this@flatMapOnChange.value.let(transform)
            current = new.onChange.listen(listener)
            listener(new.value)
        }
        return CloseableLambda {
            current.close()
            closeA.close()
        }
    }
}

fun <A, B> ObservableProperty<A>.flatMap(transform: (A) -> ObservableProperty<B>) = object : ObservableProperty<B> {
    override val onChange: Event<B> = flatMapOnChange(transform)
    override val value: B
        get() = this@flatMap.value.let(transform).value
}
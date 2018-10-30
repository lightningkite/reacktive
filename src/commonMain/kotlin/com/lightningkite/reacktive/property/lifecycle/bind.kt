package com.lightningkite.reacktive.property.lifecycle

import com.lightningkite.reacktive.Event
import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.property.ObservableProperty


fun <T> Lifecycle.bind(
        event: Event<T>,
        initialValue:T,
        listener:(T)->Unit
) = openCloseBinding(
        onOpen = { event.add(listener); listener.invoke(initialValue) },
        onClose = { event.remove(listener) }
)

fun <T> Lifecycle.bind(
        property: ObservableProperty<T>,
        listener: (T)->Unit
) = openCloseBinding(
        onOpen = { property.add(listener); listener.invoke(property.value) },
        onClose = { property.remove(listener) }
)

inline fun <A, B> Lifecycle.bind(
        propertyA: ObservableProperty<A>,
        propertyB: ObservableProperty<B>,
        crossinline listener: (A, B)->Unit
): (Boolean)->Unit {
    val a =  { it:A -> listener.invoke(it, propertyB.value) }
    val b =  { it:B -> listener.invoke(propertyA.value, it) }
    return openCloseBinding(
            onOpen = {
                propertyA.add(a)
                propertyB.add(b)
                listener.invoke(propertyA.value, propertyB.value)
            },
            onClose = {
                propertyA.remove(a)
                propertyB.remove(b)
            }
    )
}

inline fun <A, B, C> Lifecycle.bind(
        propertyA: ObservableProperty<A>,
        propertyB: ObservableProperty<B>,
        propertyC: ObservableProperty<C>,
        crossinline listener: (A, B, C)->Unit
): (Boolean)->Unit {
    val a =  { it:A -> listener.invoke(it, propertyB.value, propertyC.value) }
    val b =  { it:B -> listener.invoke(propertyA.value, it, propertyC.value) }
    val c =  { it:C -> listener.invoke(propertyA.value, propertyB.value, it) }
    return openCloseBinding(
            onOpen = {
                propertyA.add(a)
                propertyB.add(b)
                propertyC.add(c)
                listener.invoke(propertyA.value, propertyB.value, propertyC.value)
            },
            onClose = {
                propertyA.remove(a)
                propertyB.remove(b)
                propertyC.remove(c)
            }
    )
}

inline fun Lifecycle.bind(
        properties: List<ObservableProperty<out Any?>>,
        crossinline listener: ()->Unit
): (Boolean)->Unit {
    val ignoreListener = {it:Any? -> listener() }
    return openCloseBinding(
            onOpen = {
                for(prop in properties){
                    prop.add(ignoreListener)
                }
            },
            onClose = {
                for(prop in properties){
                    prop.remove(ignoreListener)
                }
            }
    )
}
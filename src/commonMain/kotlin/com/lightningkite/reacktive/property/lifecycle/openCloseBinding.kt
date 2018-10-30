package com.lightningkite.reacktive.property.lifecycle

import com.lightningkite.reacktive.Lifecycle
import com.lightningkite.reacktive.property.ObservableProperty

/**
 * Runs a lambda when the
 */
inline fun Lifecycle.openCloseBinding(
        crossinline onOpen:()->Unit,
        crossinline onClose:()->Unit
): (Boolean) -> Unit {
    var state:Boolean = false
    val lambda = { newState:Boolean ->
        if(state != newState){
            if(newState){
                onOpen()
            } else {
                onClose()
            }
            state = newState
        }
    }
    lambda(value)
    add(lambda)
    return lambda
}
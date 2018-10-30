package com.lightningkite.reacktive

/**
 * These functions are extremely handy for using collections as events.  Example:
 * <code>
 *     val onIntFound = ArrayList<(Int)->Unit>()
 *     onIntFound += { println(it) } //adds a listener
 *     onIntFound.invokeAll(1)
 *     onIntFound.invokeAll(2)
 *     onIntFound.invokeAll(3)
 * </code>
 * Created by joseph on 9/27/17.
 */

/**
 * Invokes all of the functions in this iterable.
 */
fun Iterable<() -> Unit>.invokeAll() {
    for (item in this) {
        item()
    }
}

/**
 * Invokes all of the functions in this iterable with the given arguments.
 */
fun <A> Iterable<(A) -> Unit>.invokeAll(a: A) {
    for (item in this) {
        item(a)
    }
}


/**
 * Invokes all of the functions in this iterable with the given arguments.
 */
fun <A, B> Iterable<(A, B) -> Unit>.invokeAll(a: A, b: B) {
    for (item in this) {
        item(a, b)
    }
}


/**
 * Invokes all of the functions in this iterable with the given arguments.
 */
fun <A, B, C> Iterable<(A, B, C) -> Unit>.invokeAll(a: A, b: B, c: C) {
    for (item in this) {
        item(a, b, c)
    }
}


/**
 * Invokes all of the functions in this iterable with the given arguments.
 */
fun <A, B, C, D> Iterable<(A, B, C, D) -> Unit>.invokeAll(a: A, b: B, c: C, d: D) {
    for (item in this) {
        item(a, b, c, d)
    }
}
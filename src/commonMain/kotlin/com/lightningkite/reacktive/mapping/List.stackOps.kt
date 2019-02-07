package com.lightningkite.reacktive.mapping


/**
 * Adds a state to the bottom of the stack silently.
 */
fun <E> MutableList<E>.prepend(element: E) {
    add(0, element)
}

/**
 * Pushes a new state onto the stack and notifies the listeners.
 */
fun <E> MutableList<E>.push(element: E) = add(element)

/**
 * Swaps the top state in the stack for another and notifies the listeners.
 */
fun <E> MutableList<E>.swap(element: E) {
    this[lastIndex] = element
}

/**
 * Removes and returns the element at the end of the list or returns null if the list is empty.
 */
fun <E> MutableList<E>.pop(): E? {
    return if (isNotEmpty()) removeAt(lastIndex) else null
}

/**
 * Removes and returns the element at the start of the list or returns null if the list is empty.
 */
fun <E> MutableList<E>.shift(): E? {
    return if (isNotEmpty()) removeAt(0) else null
}

/**
 * Pops all of the states off the stack except for the bottom one and notifies the listeners.
 */
fun <E> MutableList<E>.root() {
    if (size < 1) return
    val iter = listIterator(1)
    while (iter.hasNext()) {
        iter.remove()
    }
}

/**
 * Pops states off the stack until a predicate is satisfied and notifies the listeners.
 */
inline fun <E> MutableList<E>.back(predicate: (E) -> Boolean): Boolean {
    val index = indexOfFirst(predicate)
    if (index != -1) {
        val iter = listIterator(index + 1)
        while (iter.hasNext()) {
            iter.remove()
        }
    }
    return index != -1
}

/**
 * Clears the stack and starts over with a new element and notifies the listeners.
 */
fun <E> MutableList<E>.reset(element: E) {
    clear()
    add(element)
}

/**
 * Pushes a state onto the stack after [from].
 * If [from] is not on the stack, then this will do nothing.
 * If [from] is not the last item on the stack, items will be popped off until it is.
 */
fun <E> MutableList<E>.pushFrom(from: E, to: E) {
    if (back { it == from }) {
        add(to)
    }
}

/**
 * Pops the [from] state off the stack.
 * If [from] is not on the stack, then this will do nothing and return false.
 * If [from] is the last item on the stack, then this will do nothing and return false.
 * If [from] is not the last item on the stack, items will be popped off until it is.
 */
fun <E> MutableList<E>.popFrom(from: E): E? {
    return if (back { it == from }) {
        pop()
    } else null
}

/**
 * Swaps a state onto the stack in place of [from].
 * If [from] is not on the stack, then this will do nothing.
 * If [from] is not the last item on the stack, items will be popped off until it is.
 */
fun <E> MutableList<E>.swapFrom(from: E, to: E) {
    if (back { it == from }) {
        swap(to)
    }
}

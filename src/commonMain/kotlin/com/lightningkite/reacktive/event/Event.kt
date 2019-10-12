package com.lightningkite.reacktive.event

import com.lightningkite.kommon.Closeable

/**
 * An event that can occur and be observed by multiple listeners, primarily used for UI.
 * Some good examples for use of events:
 * - Some data has changed and the UI needs to update to reflect it
 * - The user clicked a button and you need to notify multiple pieces of code about it
 *
 * Comparable to Rx's Observable, but much simplified.  In comparison:
 * - Events should not ever call their listener immediately
 * - Events have no failure state, and thus no error handler
 * - Events do not have a completion
 * These simplifications are *purposeful*, and are intended to make user's lives simpler and computation more efficient.
 *
 * These are *not* meant for data processing.  Please do not use these for such purposes - instead use something else.
 */
interface Event<out T> {
    /**
     * Starts listening to an event.
     * When the event occurs, [listener] is called.
     * The returned [Closeable] will stop the [listener] from being called.
     *
     * IT IS NOT RECOMMENDED THAT YOU CALL THIS DIRECTLY because you must manage the removal of the listener manually
     * to ensure that there are no listener leaks.  Instead, it is suggested that you use a
     * [com.lightningkite.reacktive.Lifecycle] to listen to the event, which starts listening when the lifecycle turns
     * on, and stops listening when the lifecycle turns off.
     *
     * Ideally, you should listen with a lifecycle that matches the task.  For example, if you have a piece of text in
     * your UI that should show the value of something, and you have an event that fires whenever that data changes,
     * then the ideal lifecycle would be one that is on when the view is visible on the screen.
     */
    fun listen(listener: (T) -> Unit): Closeable
}

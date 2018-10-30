package com.lightningkite.reacktive.list


/**
 * A set of listeners for an observable list.
 * Created by jivie on 5/5/16.
 */
class ObservableListListenerSet<T>(
        val onAddListener: (item: T, position: Int) -> Unit,
        val onRemoveListener: (item: T, position: Int) -> Unit,
        val onChangeListener: (old: T, item: T, position: Int) -> Unit,
        val onMoveListener: (item: T, oldPosition: Int, position: Int) -> Unit,
        val onReplaceListener: (list: ObservableList<T>) -> Unit
) {
}
fun <T> ObservableList<T>.addListenerSet(set: ObservableListListenerSet<T>) {
    onListAdd.add(set.onAddListener)
    onListRemove.add(set.onRemoveListener)
    onListChange.add(set.onChangeListener)
    onListMove.add(set.onMoveListener)
    onListReplace.add(set.onReplaceListener)
}

fun <T> ObservableList<T>.removeListenerSet(set: ObservableListListenerSet<T>) {
    onListAdd.remove(set.onAddListener)
    onListRemove.remove(set.onRemoveListener)
    onListChange.remove(set.onChangeListener)
    onListMove.remove(set.onMoveListener)
    onListReplace.remove(set.onReplaceListener)
}
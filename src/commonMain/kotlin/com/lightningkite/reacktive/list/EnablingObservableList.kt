package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.EnablingObject

abstract class EnablingObservableList<E> : ObservableList<E>, EnablingObject() {

    override val onListAdd: MutableCollection<(E, Int) -> Unit> = SubEnablingCollection()
    override val onListChange: MutableCollection<(E, E, Int) -> Unit> = SubEnablingCollection()
    override val onListMove: MutableCollection<(E, Int, Int) -> Unit> = SubEnablingCollection()
    override val onListUpdate = object : SubEnablingObservableProperty<ObservableList<E>>(){
        override val value: ObservableList<E> get() = this@EnablingObservableList
    }
    override val onListReplace: MutableCollection<(ObservableList<E>) -> Unit> = SubEnablingCollection()
    override val onListRemove: MutableCollection<(E, Int) -> Unit> = SubEnablingCollection()

}

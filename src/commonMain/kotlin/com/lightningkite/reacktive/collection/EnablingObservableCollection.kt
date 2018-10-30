package com.lightningkite.reacktive.collection

import com.lightningkite.reacktive.EnablingObject

abstract class EnablingObservableCollection<E> : ObservableCollection<E>, EnablingObject() {
    override val onCollectionAdd: MutableCollection<(value: E) -> Unit> = SubEnablingCollection()
    override val onCollectionChange: MutableCollection<(old: E, new: E) -> Unit> = SubEnablingCollection()
    override val onCollectionRemove: MutableCollection<(value: E) -> Unit> = SubEnablingCollection()
    override val onCollectionUpdate = object : SubEnablingObservableProperty<ObservableCollection<E>>(){
        override val value: ObservableCollection<E> get() = this@EnablingObservableCollection
    }
    override val onCollectionReplace: MutableCollection<(ObservableCollection<E>) -> Unit> = SubEnablingCollection()
}

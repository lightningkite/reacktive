package com.lightningkite.reacktive

import com.lightningkite.reacktive.property.ObservableProperty

abstract class EnablingObject {
    abstract fun enable()
    abstract fun disable()
    abstract fun refresh()

    fun refreshIfNotActive(){
        if(listenings == 0) refresh()
    }

    val enabled: Boolean get() = listenings > 0
    protected var listenings = 0
        set(value) {
            val oldValue = field
            field = value
            if (value == 0 && oldValue != 0) {
                disable()
            }
            if (value != 0 && oldValue == 0) {
                refresh()
                enable()
            }
        }

    open inner class SubEnablingCollection<A> : EnablingMutableCollection<A>() {
        override fun enable() {
            listenings++
        }
        override fun disable() {
            listenings--
        }
    }

    abstract inner class SubEnablingObservableProperty<A> : ObservableProperty<A>, EnablingMutableCollection<(A) -> Unit>() {

        override fun enable() {
            listenings++
        }

        override fun disable() {
            listenings--
        }

        fun update(){
            for(item in this){
                item.invoke(value)
            }
        }
    }
}

package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.EnablingObject
import com.lightningkite.reacktive.property.ObservableProperty

abstract class EnablingObservableMap<K, V> : ObservableMap<K, V>, EnablingObject() {

    override val onMapPut: MutableCollection<(key: K, hadPrevious: Boolean, previous: V?, new: V) -> Unit> = SubEnablingCollection()
    override val onMapRemove: MutableCollection<(key: K, value: V) -> Unit> = SubEnablingCollection()
    override val onMapReplace: MutableCollection<(ObservableMap<K, V>) -> Unit> = SubEnablingCollection()
    override val onMapUpdate: ObservableProperty<ObservableMap<K, V>> = object : SubEnablingObservableProperty<ObservableMap<K, V>>(){
        override val value: ObservableMap<K, V> get() = this@EnablingObservableMap
    }

}

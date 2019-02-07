package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.mapping.mappingWriteOnly
import com.lightningkite.reacktive.collection.ObservableCollection
import com.lightningkite.reacktive.collection.mapping
import com.lightningkite.reacktive.property.ObservableProperty
import com.lightningkite.reacktive.property.transform
import com.lightningkite.reacktive.set.ObservableSet
import com.lightningkite.reacktive.set.mapping

open class MappingObservableMap<AK, AV, BK, BV>(
        val source: ObservableMap<AK, AV>,
        val transformKey: (AK) -> BK,
        val transformValue: (AV) -> BV,
        val reverseKey: (BK) -> AK,
        val reverseValue: (BV) -> AV
) : ObservableMap<BK, BV> {

    val transformEntry = { entry: Map.Entry<AK, AV> -> makeEntryB(entry.key.let(transformKey), entry.value.let(transformValue)) }
    val reverseEntry = { entry: Map.Entry<BK, BV> -> makeEntryA(entry.key.let(reverseKey), entry.value.let(reverseValue)) }

    fun makeEntryA(key: AK, value: AV) = object : Map.Entry<AK, AV> {
        override val key: AK
            get() = key
        override val value: AV
            get() = value
    }

    fun makeEntryB(key: BK, value: BV) = object : Map.Entry<BK, BV> {
        override val key: BK
            get() = key
        override val value: BV
            get() = value
    }

    @Suppress("UNCHECKED_CAST")
    override val entries: ObservableSet<out Map.Entry<BK, BV>> = ((source.entries) as ObservableSet<Map.Entry<AK, AV>>)
            .mapping(
                    transform = transformEntry,
                    reverse = reverseEntry
            )
    override val keys: ObservableSet<BK> = source.keys.mapping(
            transformKey,
            reverseKey
    )
    override val values: ObservableCollection<BV> = source.values.mapping(
            transformValue,
            reverseValue
    )

    override val size: Int get() = source.size
    override fun containsKey(key: BK): Boolean = source.containsKey(key.let(reverseKey))
    override fun containsValue(value: BV): Boolean = source.containsValue(value.let(reverseValue))
    override fun get(key: BK): BV? = source[key.let(reverseKey)]?.let(transformValue)
    override fun isEmpty(): Boolean = source.isEmpty()

    override val onMapPut: MutableCollection<(key: BK, hadPrevious: Boolean, previous: BV?, new: BV) -> Unit> = source.onMapPut.mappingWriteOnly { myCallback ->
        { key: AK, hadPrevious: Boolean, previous: AV?, new: AV ->
            myCallback.invoke(key.let(transformKey), hadPrevious, previous?.let(transformValue), new.let(transformValue))
        }
    }
    override val onMapRemove: MutableCollection<(key: BK, value: BV) -> Unit> = source.onMapRemove.mappingWriteOnly { myCallback ->
        { key: AK, value: AV ->
            myCallback.invoke(key.let(transformKey), value.let(transformValue))
        }
    }
    override val onMapUpdate: ObservableProperty<ObservableMap<BK, BV>> = source.onMapUpdate.transform { this }
    override val onMapReplace: MutableCollection<(ObservableMap<BK, BV>) -> Unit> = source.onMapReplace.mappingWriteOnly { myCallback ->
        { value -> myCallback.invoke(this) }
    }
}

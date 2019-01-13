package com.lightningkite.reacktive.map

import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.collection.mapping
import com.lightningkite.reacktive.set.MutableObservableSet
import com.lightningkite.reacktive.set.mapping

class MappingMutableObservableMap<AK, AV, BK, BV>(
        val mutableSource: MutableObservableMap<AK, AV>,
        transformKey: (AK)->BK,
        transformValue: (AV)->BV,
        reverseKey: (BK)->AK,
        reverseValue: (BV)->AV
) : MappingObservableMap<AK, AV, BK, BV>(
        mutableSource,
        transformKey,
        transformValue,
        reverseKey,
        reverseValue
), MutableObservableMap<BK, BV>{

    val mutableTransformEntry = { entry: MutableMap.MutableEntry<AK, AV> -> makeMutableEntryB(entry.key.let(transformKey), entry.value.let(transformValue))}
    val mutableReverseEntry = { entry: MutableMap.MutableEntry<BK, BV> -> makeMutableEntryA(entry.key.let(reverseKey), entry.value.let(reverseValue))}

    fun makeMutableEntryA(key: AK, value: AV) = object : MutableMap.MutableEntry<AK, AV> {
        override val key: AK
            get() = key
        override val value: AV
            get() = value

        override fun setValue(newValue: AV): AV {
            @Suppress("UNCHECKED_CAST")
            return mutableSource.put(key, newValue) as AV
        }
    }
    fun makeMutableEntryB(key: BK, value: BV) = object : MutableMap.MutableEntry<BK, BV> {
        override val key: BK
            get() = key
        override val value: BV
            get() = value

        @Suppress("UNCHECKED_CAST")
        override fun setValue(newValue: BV): BV {
            return put(key, newValue) as BV
        }
    }

    override val entries: MutableObservableSet<MutableMap.MutableEntry<BK, BV>> = mutableSource.entries.mapping(
            transform = mutableTransformEntry,
            reverse = mutableReverseEntry
    )
    override val keys: MutableObservableSet<BK> = mutableSource.keys.mapping(
            transformKey,
            reverseKey
    )
    override val values: MutableObservableCollection<BV> = mutableSource.values.mapping(
            transformValue,
            reverseValue
    )

    override fun replace(map: Map<BK, BV>) = mutableSource.replace(map.entries.associate { it.key.let(reverseKey) to it.value.let(reverseValue) })
    override fun clear() = mutableSource.clear()
    override fun put(key: BK, value: BV): BV? = mutableSource.put(key.let(reverseKey), value.let(reverseValue))?.let(transformValue)
    override fun putAll(from: Map<out BK, BV>) = mutableSource.putAll(from.entries.associate { it.key.let(reverseKey) to it.value.let(reverseValue) })
    override fun remove(key: BK): BV? = mutableSource.remove(key.let(reverseKey))?.let(transformValue)
}

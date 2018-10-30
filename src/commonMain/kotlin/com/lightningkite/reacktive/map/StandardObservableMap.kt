package com.lightningkite.reacktive.map

fun <K, V> StandardObservableMap(): WrapperObservableMap<K, V> = WrapperObservableMap(HashMap())
fun <K, V> StandardObservableMap(existing: Map<K, V>): WrapperObservableMap<K, V> =
    WrapperObservableMap(existing.toMutableMap())
package com.lightningkite.reacktive.list

import com.lightningkite.reacktive.property.ObservableProperty

fun <E> Array<E>.asObservableList() = ConstantObservableList(this.toList())
fun <E> List<E>.asObservableList() = ConstantObservableList(this)
fun <E> MutableList<E>.asObservableList() = WrapperObservableList(this)
fun <E> ObservableProperty<List<E>>.asObservableList() = ObservableListFromProperty(this)

fun <E> observableListOf() = ConstantObservableList(emptyList<E>())
fun <E> observableListOf(vararg elements: E) = ConstantObservableList(elements.toList())
fun <E> mutableObservableListOf(vararg elements: E) = WrapperObservableList(elements.toMutableList())
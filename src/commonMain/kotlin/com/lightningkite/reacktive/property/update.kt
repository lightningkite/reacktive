package com.lightningkite.reacktive.property

fun <T> ObservableProperty<T>.update(){
    val cached = value
    for(callback in this){
        callback.invoke(cached)
    }
}
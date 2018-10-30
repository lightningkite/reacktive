package com.lightningkite.reacktive.property

fun <A> ObservableProperty<A>.addAndInvoke(lambda:(A)->Unit){
    lambda.invoke(value)
    add(lambda)
}
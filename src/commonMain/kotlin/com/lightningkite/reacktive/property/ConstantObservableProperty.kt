package com.lightningkite.reacktive.property

import com.lightningkite.reacktive.NoOpMutableCollection


/**
 * A constant observable property - the value never changes.
 * Created by joseph on 12/2/16.
 */
class ConstantObservableProperty<T>(override val value: T) : ObservableProperty<T>, MutableCollection<(T)->Unit> by NoOpMutableCollection.type() {

}
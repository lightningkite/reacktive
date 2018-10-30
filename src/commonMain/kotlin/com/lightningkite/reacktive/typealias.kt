package com.lightningkite.reacktive

import com.lightningkite.reacktive.property.ObservableProperty

typealias Event0 = MutableCollection<()->Unit>
typealias Event<A> = MutableCollection<(A)->Unit>
typealias Event1<A> = MutableCollection<(A)->Unit>
typealias Event2<A, B> = MutableCollection<(A, B)->Unit>
typealias Event3<A, B, C> = MutableCollection<(A, B, C)->Unit>
typealias Lifecycle = ObservableProperty<Boolean>
package com.lightningkite.reacktive.set

import com.lightningkite.reacktive.collection.MutableObservableCollection
import com.lightningkite.reacktive.collection.ObservableCollection

interface ObservableSet<E> : ObservableCollection<E>, Set<E>
interface MutableObservableSet<E> : MutableObservableCollection<E>, MutableSet<E>, ObservableSet<E>
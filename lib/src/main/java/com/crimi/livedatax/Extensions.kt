package com.crimi.livedatax

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/*
 * observeOnce()
 */
fun <T : Any?> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (t: T?) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer(t)
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnce(owner: () -> Lifecycle, observer: (t: T?) -> Unit) {
    lateinit var innerObserver: (t: T?) -> Unit
    innerObserver = {
        removeObserver(innerObserver)
        observer(it)
    }
    observe(owner, innerObserver)
}

fun <T : Any?> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnce(observer: (t: T?) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer(t)
        }
    })
}

/*
 * observeOnceNotNull()
 */
fun <T : Any?> LiveData<T>.observeOnceNotNull(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer.onChanged(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnceNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnceNotNull(owner: () -> Lifecycle, observer: (t: T) -> Unit) {
    lateinit var innerObserver: (t: T?) -> Unit
    innerObserver = {
        it?.let { nonNull ->
            removeObserver(innerObserver)
            observer(nonNull)
        }
    }
    observe(owner, innerObserver)
}

fun <T : Any?> LiveData<T>.observeOnceNotNull(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer.onChanged(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeOnceNotNull(observer: (t: T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                removeObserver(this)
                observer(t)
            }
        }
    })
}

/*
 * observeUntil()
 */
fun <T : Any?> LiveData<T>.observeUntil(
    owner: LifecycleOwner,
    predicate: (t: T?) -> Boolean,
    observer: Observer<T>? = null
) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            if (predicate(t)) {
                removeObserver(this)
            } else {
                observer?.onChanged(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeUntil(
    owner: LifecycleOwner,
    predicate: (t: T?) -> Boolean,
    observer: (t: T?) -> Unit = {}
) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            if (predicate(t)) {
                removeObserver(this)
            } else {
                observer.invoke(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeUntil(
    owner: () -> Lifecycle,
    predicate: (t: T?) -> Boolean,
    observer: (t: T?) -> Unit = {}
) {
    lateinit var innerObserver: (t: T?) -> Unit
    innerObserver = {
        if (predicate(it)) {
            removeObserver(innerObserver)
        } else {
            observer.invoke(it)
        }
    }
    observe(owner, innerObserver)
}

fun <T : Any?> LiveData<T>.observeUntil(predicate: (t: T?) -> Boolean, observer: Observer<T>? = null) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            if (predicate(t)) {
                removeObserver(this)
            } else {
                observer?.onChanged(t)
            }
        }
    })
}

fun <T : Any?> LiveData<T>.observeUntil(predicate: (t: T?) -> Boolean, observer: (t: T?) -> Unit = {}) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            if (predicate(t)) {
                removeObserver(this)
            } else {
                observer.invoke(t)
            }
        }
    })
}

/*
 * observeUnique()
 */
fun <T : Any?> LiveData<T>.observeUnique(owner: LifecycleOwner, observer: Observer<T>) {
    var lastValue: T? = null
    var isSet = false
    observe(owner, Observer {
        if (!isSet || lastValue != it) {
            isSet = true
            lastValue = it
            observer.onChanged(it)
        }
    })
}

fun <T : Any?> LiveData<T>.observeUnique(owner: LifecycleOwner, observer: (T?) -> Unit) {
    var lastValue: T? = null
    var isSet = false
    observe(owner, Observer {
        if (!isSet || lastValue != it) {
            isSet = true
            lastValue = it
            observer(it)
        }
    })
}

fun <T : Any?> LiveData<T>.observeUnique(owner: () -> Lifecycle, observer: (T?) -> Unit) {
    var lastValue: T? = null
    var isSet = false
    observe(owner) {
        if (!isSet || lastValue != it) {
            isSet = true
            lastValue = it
            observer(it)
        }
    }
}
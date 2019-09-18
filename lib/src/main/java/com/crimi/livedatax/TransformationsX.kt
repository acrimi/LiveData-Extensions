package com.crimi.livedatax

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

object TransformationsX {
    fun <X, Y> mapLatest(
        vararg triggers: LiveData<out X>,
        func: (X?) -> Y?
    ): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        val observer = Observer<X> { x -> result.value = func.invoke(x) }

        for (trigger in triggers) {
            result.addSource(trigger, observer)
        }

        return result
    }

    fun <X, Y> mapAll(
        vararg triggers: LiveData<out X>,
        func: (List<X?>) -> Y?
    ): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        val observer = Observer<X> { _ ->
            result.value = func.invoke(triggers.map { it.value })
        }

        for (trigger in triggers) {
            result.addSource(trigger, observer)
        }

        return result
    }

    fun <X, Y> debounce(
        trigger: LiveData<X>,
        func: (X?) -> LiveData<Y>?,
        delay: Long
    ): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        val handler = Handler()
        var mSource: LiveData<Y>? = null

        result.addSource(trigger) { x ->
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                val newLiveData = func.invoke(x)
                if (mSource == newLiveData) {
                    return@postDelayed
                }
                mSource?.let {
                    result.removeSource(it)
                }
                mSource = newLiveData
                mSource?.let { source ->
                    result.addSource(source) {
                        result.value = it
                    }
                }
            }, delay)
        }

        return result
    }
}
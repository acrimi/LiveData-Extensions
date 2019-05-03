package com.crimi.livedatax

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class CompoundLiveData(private val setType: Set = Set.ALL) : LiveData<Boolean>() {

    enum class Set {
        ALL, ANY, ONE
    }

    private val mediator = MediatorLiveData<Boolean>()
    private val sources = mutableListOf<LiveData<Boolean>>()
    private val mediatorObserver = Observer<Boolean> {
        postValue(it)
    }
    private val sourceObserver = Observer<Boolean> {
        val result = when (setType) {
            Set.ALL -> sources.all { it.value == true }
            Set.ANY -> sources.any { it.value == true }
            Set.ONE -> sources.singleOrNull { it.value == true } != null
        }
        mediator.postValue(result)
    }

    fun addSource(source: LiveData<Boolean>) {
        sources.add(source)
        mediator.addSource(source, sourceObserver)
    }

    fun removeSource(source: LiveData<Boolean>) {
        sources.remove(source)
        mediator.removeSource(source)
        // reevaluate current state
        sourceObserver.onChanged(true)
    }

    override fun onInactive() {
        mediator.removeObserver(mediatorObserver)
        super.onInactive()
    }

    override fun onActive() {
        mediator.observeForever(mediatorObserver)
        super.onActive()
    }
}
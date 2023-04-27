package com.volio.vn.b1_project.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel(){
    protected val loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = loading
}
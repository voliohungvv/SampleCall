package com.volio.vn.b1_project.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.volio.vn.b1_project.base.BaseViewModel
import com.volio.vn.data.models.BitcoinModel
import com.volio.vn.data.usecases.BitcoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bitcoinUseCase: BitcoinUseCase
) : BaseViewModel() {

    private val _bitcoinList = MutableLiveData<List<BitcoinModel>>()
    val bitcoinList: LiveData<List<BitcoinModel>> = _bitcoinList

    fun getBitcoinList() = viewModelScope.launch {
        loading.value = true
        val bitcoinList =
            withContext(Dispatchers.IO) { bitcoinUseCase.getBitcoinPrices() }
        _bitcoinList.value = bitcoinList
        loading.value = false

        Log.e(TAG, "getBitcoinList : $bitcoinList")
    }


    fun removeItem(index: Int) {
        bitcoinList.value?.let {
            val updatedList = it.toMutableList()
            val a = updatedList.removeAt(index)
            _bitcoinList.value = updatedList
        }
    }
}
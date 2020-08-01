package com.codelectro.invoicemaker.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.model.Price
import javax.inject.Singleton

@Singleton
class InvoiceViewModel @ViewModelInject constructor() : ViewModel() {

    var id: Long = 0

    val _price: MutableLiveData<Price> = MutableLiveData()
    val price: LiveData<Price>
        get() = _price

    val isPrimaryUnit: MutableLiveData<Boolean> = MutableLiveData()

    var lineList = mutableListOf<LineItem>()


    val _itemLineList: MutableLiveData<List<LineItem>> = MutableLiveData()
    val itemLineList: LiveData<List<LineItem>>
        get() = _itemLineList

    init {
    }

    fun upDateList() {
        _itemLineList.value = lineList
    }

}
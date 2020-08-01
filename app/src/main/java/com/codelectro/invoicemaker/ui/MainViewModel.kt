package com.codelectro.invoicemaker.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product
import com.codelectro.invoicemaker.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    fun insertProduct(product: Product) = viewModelScope.launch {
        repository.insertProduct(product)
    }

    fun getProducts() = repository.getProducts()

    fun insertItem(item: Item): LiveData<Long> {
        val itemId = MutableLiveData<Long>()
        viewModelScope.launch {
            val id = repository.insertItem(item)
            itemId.postValue(id)
        }
        return itemId
    }

    fun updateItem(item: Item) = viewModelScope.launch {
        repository.updateItem(item)
    }

    fun deleteItem(item: Item) = viewModelScope.launch {
        repository.deleteItem(item)
    }

    fun getItems() = repository.getItems()
    fun getItem(id: Long) = repository.getItem(id)

    fun insertLineItem(lineItem: LineItem) = viewModelScope.launch {
        repository.insertLineItem(lineItem)
    }

    fun deleteLineItem(item: LineItem) = viewModelScope.launch {
        repository.deleteLineItem(item)
    }

    fun updateLineItem(item: LineItem) = viewModelScope.launch {
        repository.updateLineItem(item)
    }

    fun getLineItem(itemId: Long) = repository.getLineItems(itemId)


}
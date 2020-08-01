package com.codelectro.invoicemaker.repository

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codelectro.invoicemaker.db.MainDao
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val mainDao: MainDao
) {

    suspend fun insertProduct(product: Product) =
        mainDao.insertProduct(product)

    fun getProducts() = mainDao.getProducts()
    fun getProduct(id: Long) = mainDao.getProduct(id)

    suspend fun insertItem(item: Item) = mainDao.insertItem(item)
    suspend fun updateItem(item: Item) = mainDao.updateItem(item)
    suspend fun deleteItem(item: Item) = mainDao.deleteItem(item)
    fun getItems() = mainDao.getItems()
    fun getItem(id: Long) = mainDao.getItem(id)

    suspend fun insertLineItem(item: LineItem) = mainDao.insertLineItem(item)
    suspend fun deleteLineItem(item: LineItem) = mainDao.deleteLineItem(item)
    suspend fun updateLineItem(item: LineItem) = mainDao.updateLineItem(item)
    fun getLineItems(itemId: Long) = mainDao.getLineItems(itemId)
    fun getLineItem(lineItemId: Long) = mainDao.getLineItem(lineItemId)
}
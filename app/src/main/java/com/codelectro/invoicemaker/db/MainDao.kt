package com.codelectro.invoicemaker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product

@Dao
interface MainDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products")
    fun getProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProduct(id: Long): LiveData<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Query("SELECT * FROM items")
    fun getItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItem(id: Long): LiveData<Item>

    @Delete
    suspend fun deleteItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineItem(item: LineItem)

    @Delete
    suspend fun deleteLineItem(item: LineItem)

    @Update
    suspend fun updateLineItem(item: LineItem)

    @Query("SELECT * FROM line_items WHERE itemId = :itemId")
    fun getLineItems(itemId: Long): LiveData<List<LineItem>>

    @Query("SELECT * FROM line_items WHERE id = :lineItemId")
    fun getLineItem(lineItemId: Long): LiveData<LineItem>

}

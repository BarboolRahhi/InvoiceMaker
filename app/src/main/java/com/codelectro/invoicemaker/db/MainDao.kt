package com.codelectro.invoicemaker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codelectro.invoicemaker.entity.*

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

    @Query("DELETE FROM line_items WHERE itemId = :itemId")
    suspend fun deleteLineItemByItemId(itemId: Long)

    @Update
    suspend fun updateLineItem(item: LineItem)

    @Query("SELECT * FROM line_items WHERE itemId = :itemId")
    fun getLineItems(itemId: Long): LiveData<List<LineItem>>

    @Query("SELECT * FROM line_items WHERE id = :lineItemId")
    fun getLineItem(lineItemId: Long): LiveData<LineItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Long): LiveData<User>

    @Transaction
    @Query("SELECT * FROM users")
    fun getUsersAndItems(): LiveData<List<UserAndItem>>

}

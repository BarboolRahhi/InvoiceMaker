package com.codelectro.invoicemaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product
import com.codelectro.invoicemaker.entity.User

@Database(
    entities = [Product::class, Item::class, LineItem::class, User::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDatabase : RoomDatabase() {

    abstract fun getMainDao() : MainDao
}
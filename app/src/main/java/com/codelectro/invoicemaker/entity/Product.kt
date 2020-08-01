package com.codelectro.invoicemaker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val barCode: Long,
    val primaryUnit: String,
    val secondaryUnit: String,
    val converterValue: Int,
    val purchasePrice: Float,
    val sellingPrice: Float,
    val quantity: Int = 0
)
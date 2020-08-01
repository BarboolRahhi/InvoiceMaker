package com.codelectro.invoicemaker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "line_items")
data class LineItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val subTotal: Float = 0.0f,
    val quantity: Int = 0,
    val total: Float = 0.0f,
    val discount: Float = 0.0f,
    val productId: Long,
    val itemId: Long? = null
) {
}
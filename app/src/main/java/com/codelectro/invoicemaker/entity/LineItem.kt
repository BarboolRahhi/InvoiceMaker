package com.codelectro.invoicemaker.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "line_items",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("itemId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class LineItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val subTotal: Float = 0.0f,
    val quantity: Int = 0,
    val unit: String,
    val total: Float = 0.0f,
    val discount: Float = 0.0f,
    val productId: Long,
    val itemId: Long? = null
) : Serializable
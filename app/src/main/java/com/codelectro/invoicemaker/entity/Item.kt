package com.codelectro.invoicemaker.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*


@Entity(
    tableName = "items", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val subTotal: Float = 0.0f,
    val discount: Float = 0.0f,
    val total: Float = 0.0f,
    val paid: Float = 0.0f,
    val totalLineItem: Int = 0,
    val invoiceNumber: Long = 0,
    val balance: Float = 0.0f,
    val date: Date = Date(),
    val userId: Long? = null
) : Serializable
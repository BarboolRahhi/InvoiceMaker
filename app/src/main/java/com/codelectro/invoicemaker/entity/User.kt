package com.codelectro.invoicemaker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val customerName: String,
    val phoneNo: Long? = null,
    val address: String? = null,
    val email: String? = null
) : Serializable
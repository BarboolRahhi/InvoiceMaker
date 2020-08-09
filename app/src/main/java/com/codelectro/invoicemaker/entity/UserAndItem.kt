package com.codelectro.invoicemaker.entity

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class UserAndItem(
    @Embedded val user: User? = null,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val item: Item
): Serializable
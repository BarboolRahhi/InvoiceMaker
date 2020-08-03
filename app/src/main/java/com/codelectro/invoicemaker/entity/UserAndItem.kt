package com.codelectro.invoicemaker.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndItem(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val item: Item
)
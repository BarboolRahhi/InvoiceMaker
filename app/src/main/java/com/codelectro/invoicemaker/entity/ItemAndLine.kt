package com.codelectro.invoicemaker.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ItemAndLine(
    @Embedded val item: Item,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId"
    )
    val lineItems: List<LineItem> = mutableListOf()
)
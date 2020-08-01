package com.codelectro.invoicemaker.adapter

import android.view.View
import com.codelectro.invoicemaker.entity.LineItem

interface RecycleViewClickListener<T> {
    fun onClick(view: View, data: T)
    fun onDeleteClick(view: View, data: T)
}
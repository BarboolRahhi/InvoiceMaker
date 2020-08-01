package com.codelectro.invoicemaker.ui

import android.content.Context
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Double.formatNumber(): String {
    return NumberFormat.getNumberInstance().format(this).toString()
}

fun String.dateTimeFormat(): String {
    val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).parse(this)
    date?.let {
        return SimpleDateFormat("dd MMM, yyyy h:mm a", Locale.ENGLISH).format(date)
    }
    return "No Date Time"
}

fun String.color(color: String): String {
    return "<font color=$color>$this</font>"
}
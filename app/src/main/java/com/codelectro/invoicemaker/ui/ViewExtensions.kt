package com.codelectro.invoicemaker.ui

import android.content.Context
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Double.formatNumber(): String {
    return NumberFormat.getNumberInstance().format(this).toString()
}

fun Date.dateTimeFormat(): String {
    val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)
    return formatter.format(this)
}

fun String.color(color: String): String {
    return "<font color=$color>$this</font>"
}

fun Float.roundDecimal(): Float {
    return ((this * 100.0).roundToInt() / 100.0).toFloat()
}
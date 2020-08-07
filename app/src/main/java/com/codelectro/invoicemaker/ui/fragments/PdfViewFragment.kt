package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.ui.MainActivity
import java.io.File

class PdfViewFragment : Fragment() {

    private val DEST  = "/sdcard/test1.pdf"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)

    }


}
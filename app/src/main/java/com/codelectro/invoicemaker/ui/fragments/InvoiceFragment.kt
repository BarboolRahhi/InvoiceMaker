package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.ui.InvoiceViewModel
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_invoice.*
import timber.log.Timber

@AndroidEntryPoint
class InvoiceFragment : Fragment(R.layout.fragment_invoice) {

    private val viewmodel: MainViewModel by viewModels()
    private val invoiceViewModel: InvoiceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)

        val navController = Navigation.findNavController(view)

        viewmodel.getItems().observe(viewLifecycleOwner, Observer {
            Timber.d("InvoiceFragment: $it")
        })

        new_invoice_btn.setOnClickListener {
            viewmodel.insertItem(Item()).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    requireContext().showToast("$it")
                    val action = InvoiceFragmentDirections.actionInvoiceFragmentToBillFragment(it)
                    navController.navigate(action)
                }
            })
        }
    }

}
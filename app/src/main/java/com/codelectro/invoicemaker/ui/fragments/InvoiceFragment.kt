package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.InvoiceRecyclerViewAdapter
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.ui.InvoiceViewModel
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.showToast
import com.codelectro.invoicemaker.util.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_invoice.*
import timber.log.Timber

@AndroidEntryPoint
class InvoiceFragment : Fragment(R.layout.fragment_invoice) {

    private val viewmodel: MainViewModel by viewModels()
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private lateinit var invoiceAdapter: InvoiceRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)

        val navController = Navigation.findNavController(view)

        invoiceAdapter = InvoiceRecyclerViewAdapter()

        viewmodel.getUsersAndItems().observe(viewLifecycleOwner, Observer {
            Timber.d("InvoiceFragment: $it")
            invoiceAdapter.submitList(it)
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

        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        recyclerView.apply {
            adapter = invoiceAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(VerticalSpacingItemDecoration(40))
        }
    }

}
package com.codelectro.invoicemaker.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.InvoiceRecyclerViewAdapter
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.UserAndItem
import com.codelectro.invoicemaker.ui.InvoiceViewModel
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.showToast
import com.codelectro.invoicemaker.util.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_invoice.*
import timber.log.Timber


@AndroidEntryPoint
class InvoiceFragment : Fragment(R.layout.fragment_invoice),
    InvoiceRecyclerViewAdapter.InvoiceClickListener {

    private val viewmodel: MainViewModel by viewModels()
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private lateinit var invoiceAdapter: InvoiceRecyclerViewAdapter
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)

        navController = Navigation.findNavController(view)

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

        invoiceAdapter.setOnInvoiceClickListener(this)
        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        recyclerView.apply {
            adapter = invoiceAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(VerticalSpacingItemDecoration(40))
        }
    }

    override fun onMenuClick(view: View, userAndItem: UserAndItem) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.invoice_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.edit -> {
                    val action = userAndItem.item.id?.let {
                        InvoiceFragmentDirections.actionInvoiceFragmentToBillFragment(it)
                    }
                    navController.navigate(action!!)
                    return@setOnMenuItemClickListener true
                }
                R.id.delete -> {
                    userAndItem.user?.let { user -> viewmodel.deleteUser(user) }
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

}
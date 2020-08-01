package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.LineItemRecyclerViewAdapter
import com.codelectro.invoicemaker.adapter.RecycleViewClickListener
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.ui.InvoiceViewModel
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.util.VerticalSpacingItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bill.*
import timber.log.Timber
import kotlin.properties.Delegates


@AndroidEntryPoint
class BillFragment : Fragment(R.layout.fragment_bill), RecycleViewClickListener<LineItem> {

    private val viewmodel: MainViewModel by viewModels()
    private lateinit var lineAdapter: LineItemRecyclerViewAdapter
    private val invoiceViewModel: InvoiceViewModel by viewModels()

    private var itemId by Delegates.notNull<Long>()
    private lateinit var navController: NavController

    val item = MutableLiveData<Item>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setDrawerEnabled(false)

        navController = Navigation.findNavController(view)
        lineAdapter = LineItemRecyclerViewAdapter()

        arguments?.let {
            val args = BillFragmentArgs.fromBundle(requireArguments())
            itemId = args.itemId
        }


        viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
            item.postValue(it)
            Timber.d("TAG - add_item $it")
        })

        add_item.setOnClickListener {
            val action = BillFragmentDirections.actionBillFragmentToAddItemFragment(itemId, -1)
            navController.navigate(action)
        }

        viewmodel.getLineItem(itemId).removeObservers(viewLifecycleOwner)
        viewmodel.getLineItem(itemId).observe(viewLifecycleOwner, Observer {
            Timber.d("TAG - bill $it")
            lineAdapter.submitList(it)
        })

        viewmodel.getItem(itemId).removeObservers(viewLifecycleOwner)
        viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
            Timber.d("TAG - bill $it")
            it?.let {
                tvSubTotal.text = "Rs.${it.subTotal}"
                tvDiscount.text = "Rs.${it.discount}"
                tvTotal.text = "Rs.${it.total}"
                tvItemCount.text = "Item List (${it.totalLineItem})"
            }
        })

        cbRecived.setOnCheckedChangeListener { buttonView, isChecked ->
            if (cbRecived.isChecked) {
                etPaid.setText(tvTotal.text.substring(3, tvTotal.text.length))
            } else {
                etPaid.setText("0.0")
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Exit Invoice")
                    .setMessage("Cancel will delete your current invoice")
                    .setNegativeButton("Continue") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setPositiveButton("Cancel") { dialog, _ ->
                        viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
                            viewmodel.deleteItem(it)
                            findNavController().navigate(R.id.action_billFragment_to_invoiceFragment)
                        })
                    }
                    .create()
                dialog.show()
            }

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        lineAdapter.setOnClickItemListener(this)
        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        recyclerview.apply {
            adapter = lineAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(VerticalSpacingItemDecoration(20))
        }
    }

    override fun onClick(view: View, data: LineItem) {
        val action = BillFragmentDirections.actionBillFragmentToAddItemFragment(itemId, data.id!!)
        navController.navigate(action)
    }

    override fun onDeleteClick(view: View, data: LineItem) {
        item.value?.let{
            val updateItem = Item(
                id = it.id,
                subTotal = (it.subTotal - data.subTotal),
                discount = (it.discount - data.discount),
                total = (it.total - data.total ),
                totalLineItem = (it.totalLineItem - 1)
            )
            Timber.d("TAG - add_item $it")
            viewmodel.updateItem(updateItem)
            viewmodel.deleteLineItem(data)
        }

    }

}
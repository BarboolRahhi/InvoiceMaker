package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
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
import com.codelectro.invoicemaker.ui.*
import com.codelectro.invoicemaker.util.VerticalSpacingItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_invoice_editor.*
import timber.log.Timber
import kotlin.properties.Delegates


@AndroidEntryPoint
class InvoiceEditorFragment : Fragment(R.layout.fragment_invoice_editor),
    RecycleViewClickListener<LineItem> {

    private val viewmodel: MainViewModel by viewModels()
    private lateinit var lineAdapter: LineItemRecyclerViewAdapter
    private val invoiceViewModel: InvoiceViewModel by viewModels()

    private var itemId by Delegates.notNull<Long>()
    private lateinit var navController: NavController

    val item = MutableLiveData<Item>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setDrawerEnabled(false)

        navController = Navigation.findNavController(view)
        lineAdapter = LineItemRecyclerViewAdapter()

        arguments?.let {
            val args = InvoiceEditorFragmentArgs.fromBundle(requireArguments())
            this.itemId = args.itemId
        }


        add_item.setOnClickListener {
            val bundle = bundleOf("itemId" to this.itemId)
            findNavController().navigate(R.id.action_billFragment_to_addItemFragment, bundle)
        }


        viewmodel.getItemAndLineItems(this.itemId).observe(viewLifecycleOwner, Observer {
            item.postValue(it.item)
            Timber.d("TAG - ItemAndLine $it")
            it.item.apply {
                tvSubTotal.text = "Rs.${subTotal.roundDecimal()}"
                tvDiscount.text = "Rs.${discount.roundDecimal()}"
                tvTotal.text = "Rs.${total.roundDecimal()}"
                tvItemCount.text = "Item List ($totalLineItem)"
            }
            lineAdapter.submitList(it.lineItems)
        })


        save_invoice_btn.setOnClickListener {
            viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
                if (it.totalLineItem >= 1) {
                    val bundle = bundleOf().apply {
                        putSerializable("item", it)
                    }
                    findNavController().navigate(
                        R.id.action_billFragment_to_customerDetailFragment,
                        bundle
                    )
                } else {
                    requireContext().showToast("There is no item for bill. Add Item at least one Item")
                }

            })

        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
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
        val bundle = bundleOf("itemId" to itemId).apply {
            putSerializable("lineItem", data)
        }
        findNavController().navigate(R.id.action_billFragment_to_addItemFragment, bundle)
    }

    override fun onDeleteClick(view: View, data: LineItem) {
        item.value?.let {
            val updateItem = Item(
                id = it.id,
                subTotal = (it.subTotal - data.subTotal),
                discount = (it.discount - data.discount),
                total = (it.total - data.total),
                totalLineItem = (it.totalLineItem - 1)
            )
            Timber.d("TAG - add_item $it")
            viewmodel.updateItem(updateItem)
            viewmodel.deleteLineItem(data)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    private fun showDialog() {
        val action = if (item.value?.userId == null) "Cancel" else "Exit"
        val message =
            if (item.value?.userId == null) "Cancel will delete your current invoice"
            else "Exit current invoice"

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Exit Invoice")
            .setMessage(message)
            .setNegativeButton("Continue") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(action) { _, _ ->
                viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
                    if (it.userId == null) {
                        viewmodel.deleteItem(it)
                        findNavController().navigate(R.id.action_billFragment_to_invoiceFragment)
                    } else {
                        findNavController().navigate(R.id.action_billFragment_to_invoiceFragment)
                    }
                })
            }
            .create()
        dialog.show()
    }

}
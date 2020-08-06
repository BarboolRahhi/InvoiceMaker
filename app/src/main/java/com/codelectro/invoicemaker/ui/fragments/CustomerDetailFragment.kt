package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.User
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_customer_detail.*
import timber.log.Timber


@AndroidEntryPoint
class CustomerDetailFragment : Fragment(R.layout.fragment_customer_detail) {

    private val viewmodel: MainViewModel by viewModels()
    private val args: CustomerDetailFragmentArgs by navArgs()
    private lateinit var item: Item

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)

        item = args.item

        tvTotal.text = "RS.${item.total}"
        Timber.d("User: $item")

        item.userId?.let { id ->
            save_invoice_btn.text = "Update Invoice"
            viewmodel.getUser(id).observe(viewLifecycleOwner, Observer {
                Timber.d("User: $it")
                it.apply {
                    etCustomerName.setText(customerName)
                    etAddress.setText(address)
                    if (phoneNo != null)
                        etPhone.setText(phoneNo.toString())
                    etEmail.setText(email)
                    cbRecived.isChecked = true
                    etPaid.setText(item.paid.toString())
                }
            })
        }

        etPaid.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val paid = etPaid.text.toString()
                if (paid.isNotEmpty()) {
                    etBalance.setText("${item.total - paid.toFloat()}")
                }

            }

        })

        save_invoice_btn.setOnClickListener {
            if (etCustomerName.text.toString().isEmpty()) {
                etCustomerName.error = "Customer Name Required"
                return@setOnClickListener
            }

            if (etPaid.text.toString().isNotEmpty() && etPaid.text.toString().toFloat() >= 0.0f && cbRecived.isChecked) {

                val phone = if (etPhone.text.toString().isNotEmpty()) etPhone.text.toString()
                    .toLong() else null
                if (item.userId == null) {
                    val user = User(
                        customerName = etCustomerName.text.toString(),
                        address = etAddress.text.toString(),
                        email = etEmail.text.toString(),
                        phoneNo = phone
                    )
                    viewmodel.insertUser(user).observe(viewLifecycleOwner, Observer { userId ->
                        item?.let {
                            val updateItem = Item(
                                id = it.id,
                                invoiceNumber = it.id!!,
                                subTotal = it.subTotal,
                                discount = it.discount,
                                total = it.total,
                                totalLineItem = it.totalLineItem,
                                balance = etBalance.text.toString().toFloat(),
                                paid = etPaid.text.toString().toFloat(),
                                userId = userId
                            )
                            viewmodel.updateItem(updateItem)
                            findNavController().navigate(R.id.action_customerDetailFragment_to_invoiceFragment)
                        }
                    })
                } else {
                    val user = User(
                        id = item.userId,
                        customerName = etCustomerName.text.toString(),
                        address = etAddress.text.toString(),
                        email = etEmail.text.toString(),
                        phoneNo = phone
                    )
                    viewmodel.updateUser(user)
                    item.let {
                        val updateItem = Item(
                            id = it.id,
                            invoiceNumber = it.id!!,
                            subTotal = it.subTotal,
                            discount = it.discount,
                            total = it.total,
                            totalLineItem = it.totalLineItem,
                            balance = etBalance.text.toString().toFloat(),
                            paid = etPaid.text.toString().toFloat(),
                            userId = item.userId
                        )
                        viewmodel.updateItem(updateItem)
                        findNavController().navigate(R.id.action_customerDetailFragment_to_invoiceFragment)
                    }
                }


            } else {
                etPaid.error = "Add paid amount"
                requireContext().showToast("Check the payment received")
            }

        }


        cbRecived.setOnCheckedChangeListener { _, _ ->
            if (cbRecived.isChecked) {
                etPaid.setText(tvTotal.text.substring(3))
                etBalance.setText("0.0")
            } else {
                etPaid.setText("0.0")
            }
        }

    }
}
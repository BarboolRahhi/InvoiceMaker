package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.DropDownAdapter
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product
import com.codelectro.invoicemaker.model.Price
import com.codelectro.invoicemaker.ui.InvoiceViewModel
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.formatNumber
import com.codelectro.invoicemaker.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_item.*
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private val viewmodel: MainViewModel by viewModels()
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private lateinit var product: Product
    private var subTotal: Float = 0.0f
    private var total: Float = 0.0f
    private var discountPrice: Float = 0.0f

    private var itemId by Delegates.notNull<Long>()

    val item = MutableLiveData<Item>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        arguments?.let {
            val args = BillFragmentArgs.fromBundle(requireArguments())
            itemId = args.itemId
        }

        setAllFieldVisible(false)

        viewmodel.getProducts().observe(viewLifecycleOwner, Observer {
            Timber.d("products: $it")
            setProducts(it)
        })

        viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
            item.postValue(it)
            Timber.d("TAG - add_item $it")
        })

        invoiceViewModel.price.observe(viewLifecycleOwner, Observer {
            setSubTotalPrice(it.subTotal, it.total)
        })

        radioGroupUnit.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioPrimaryUnit -> {
                    setPrice(product)
                    invoiceViewModel.isPrimaryUnit.value = true
                }
                R.id.radioSecondaryUnit -> {
                    setPrice(product)
                    invoiceViewModel.isPrimaryUnit.value = false
                }
            }
        }

        etQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setPrice(product)
            }

        })

        etDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setPrice(product)
            }

        })

        add_item.setOnClickListener {
            val quantity = etQuantity.text.toString()
            if (quantity.isNotEmpty() && (radioPrimaryUnit.isChecked || radioSecondaryUnit.isChecked)) {
                val lineItem = LineItem(
                    subTotal = subTotal,
                    total = total,
                    name = product.name,
                    quantity = quantity.toInt(),
                    discount = discountPrice,
                    productId = product.id!!,
                    itemId = itemId
                )
                viewmodel.insertLineItem(lineItem)
                viewmodel.getItem(itemId).removeObservers(viewLifecycleOwner)
                viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
                    if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        val updateItem = Item(
                            id = it.id,
                            subTotal = (subTotal + it.subTotal),
                            discount = (discountPrice + it.discount),
                            total = (total + it.total),
                            totalLineItem = (it.totalLineItem + 1)
                        )
                        viewmodel.updateItem(updateItem)
                    }
                    val action =
                        AddItemFragmentDirections.actionAddItemFragmentToBillFragment(itemId)
                    navController.navigate(action)
                })
            } else {
                requireContext().showToast("Please select Unit and fill Quantity")
            }

        }

        save_item_continue.setOnClickListener {
            val quantity = etQuantity.text.toString()
            if (quantity.isNotEmpty() && (radioPrimaryUnit.isChecked || radioSecondaryUnit.isChecked)) {

                val lineItem = LineItem(
                    subTotal = subTotal,
                    total = total,
                    name = product.name,
                    discount = discountPrice,
                    quantity = quantity.toInt(),
                    productId = product.id!!,
                    itemId = itemId
                )

                item.value?.let{
                    val updateItem = Item(
                        id = it.id,
                        subTotal = (subTotal + it.subTotal),
                        discount = (discountPrice + it.discount),
                        total = (total + it.total),
                        totalLineItem = (it.totalLineItem + 1)
                    )
                    Timber.d("TAG - add_item $it")
                    viewmodel.updateItem(updateItem)
                    viewmodel.insertLineItem(lineItem)
                    resetForm()
                }
            } else {
                requireContext().showToast("Please select Unit and fill Quantity")
            }

        }

    }

    private fun resetForm() {
        etProduct.setText("")
        etQuantity.setText("")
        textInputDiscount.helperText = ""
        etDiscount.setText("")
        radioGroupUnit.clearCheck()
        invoiceViewModel._price.postValue(
            Price(
                subTotal = 0.0f,
                total = 0.0f
            )
        )
    }

    private fun setProducts(products: List<Product>) {
        val arrayProduct = products.map { product -> product.name }.toTypedArray()

        val adapter = DropDownAdapter(requireContext(), products)
        etProduct.setAdapter(adapter)

        etProduct.setOnItemClickListener { parent, view, position, id ->
            val selection = parent.getItemAtPosition(position) as Product
            setAllFieldVisible(true)
            product = selection
            setUnit(selection)
            setPrice(selection)
        }

    }

    private fun setUnit(product: Product) {
        radioPrimaryUnit.text = product.primaryUnit
        radioSecondaryUnit.text = product.secondaryUnit
    }


    private fun setPrice(product: Product) {


        val quantity = etQuantity.text.toString()
        var discountPerCent = etDiscount.text.toString()

        if (quantity.isNotEmpty()) {

            if (discountPerCent.isEmpty()) discountPerCent = "0"

            val quantityFloat = quantity.toFloat()
            var discountPerCentFloat = discountPerCent.toFloat()

            invoiceViewModel.isPrimaryUnit.observe(viewLifecycleOwner, Observer {
                if (it) {
                    subTotal = (product.sellingPrice * quantityFloat)

                    discountPrice =
                        (((discountPerCent.toFloat() / 100) * (subTotal)))

                    total = subTotal.minus(discountPrice)

                    textInputDiscount.helperText =
                        "Rs.${(Math.round(discountPrice * 100.0) / 100.0).formatNumber()}"

                    invoiceViewModel._price.postValue(
                        Price(
                            subTotal = subTotal,
                            total = total
                        )
                    )
                } else {
                    subTotal =
                        ((product.sellingPrice / product.converterValue) * quantityFloat)
                    discountPrice =
                        (((discountPerCent.toFloat() / 100) * (subTotal)))
                    textInputDiscount.helperText =
                        "Rs.${(Math.round(discountPrice * 100.0) / 100.0).formatNumber()}"
                    total = subTotal.minus(discountPrice)
                    invoiceViewModel._price.postValue(
                        Price(
                            subTotal = subTotal,
                            total = total
                        )
                    )
                }
            })
        }

    }

    private fun setSubTotalPrice(subTotal: Float, total: Float) {
        val stringSubTotal = (Math.round(subTotal * 100.0) / 100.0).formatNumber()
        val stringTotal = (Math.round(total * 100.0) / 100.0).formatNumber()
        tvSubTotal.text = "Rs.$stringSubTotal"
        tvTotal.text = "Rs.$stringTotal"
    }

    private fun setAllFieldVisible(isVisible: Boolean) {
        radioGroupUnit.visibility = if (isVisible) View.VISIBLE else View.GONE
        textInputQuantity.visibility = if (isVisible) View.VISIBLE else View.GONE
        textInputDiscount.visibility = if (isVisible) View.VISIBLE else View.GONE
        subTotalLable.visibility = if (isVisible) View.VISIBLE else View.GONE
        tvSubTotal.visibility = if (isVisible) View.VISIBLE else View.GONE
        totalLable.visibility = if (isVisible) View.VISIBLE else View.GONE
        tvTotal.visibility = if (isVisible) View.VISIBLE else View.GONE
        add_item.visibility = if (isVisible) View.VISIBLE else View.GONE
        save_item_continue.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


}
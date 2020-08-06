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
import androidx.navigation.fragment.navArgs
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.DropDownAdapter
import com.codelectro.invoicemaker.entity.Item
import com.codelectro.invoicemaker.entity.LineItem
import com.codelectro.invoicemaker.entity.Product
import com.codelectro.invoicemaker.model.Price
import com.codelectro.invoicemaker.ui.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_item.*
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private val viewmodel: MainViewModel by viewModels()
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private var product: Product? = null
    private var subTotal: Float = 0.0f
    private var total: Float = 0.0f
    private var discountPrice: Float = 0.0f
    private var unit: String? = null

    private var itemId by Delegates.notNull<Long>()
    private var lineItem: LineItem? = null

    val item = MutableLiveData<Item>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAllFieldVisible(false)

        val navController = Navigation.findNavController(view)

        itemId = arguments?.getLong("itemId")!!
        lineItem = arguments?.getSerializable("lineItem") as LineItem?

        if (lineItem != null) {
            Timber.d("Data: $lineItem")
            setEditProduct(lineItem?.id!!)
            add_item.text = "Update Item"
        }

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
                    product?.let { setPrice(it) }
                    invoiceViewModel.isPrimaryUnit.value = true
                }
                R.id.radioSecondaryUnit -> {
                    product?.let { setPrice(it) }
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
                product?.let { setPrice(it) }
            }

        })

        etDiscount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                product?.let { setPrice(it) }
            }

        })

        add_item.setOnClickListener {
            val quantity = etQuantity.text.toString()
            if (quantity.isNotEmpty() && (radioPrimaryUnit.isChecked || radioSecondaryUnit.isChecked)) {
                val lineItem = if (this.lineItem == null) {
                    LineItem(
                        subTotal = subTotal,
                        total = total,
                        name = product!!.name,
                        unit = unit!!,
                        quantity = quantity.toInt(),
                        discount = discountPrice,
                        productId = product!!.id!!,
                        itemId = itemId
                    )
                } else {
                    LineItem(
                        id = this.lineItem!!.id,
                        subTotal = subTotal,
                        total = total,
                        name = product!!.name,
                        unit = unit!!,
                        quantity = quantity.toInt(),
                        discount = discountPrice,
                        productId = product!!.id!!,
                        itemId = itemId
                    )
                }

                viewmodel.insertLineItem(lineItem)

                if (this.lineItem == null) {
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
                    viewmodel.getItem(itemId).observe(viewLifecycleOwner, Observer {
                        Timber.d("Data: $it")
                        Timber.d("Data: subTotal $subTotal")
                        if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            val updateItem = Item(
                                id = it.id,
                                subTotal = (subTotal + it.subTotal) - this.lineItem!!.subTotal,
                                discount = (discountPrice + (it.discount - this.lineItem!!.discount)),
                                total = (total + (it.total - this.lineItem!!.total)),
                                totalLineItem = (it.totalLineItem),
                                userId = it.userId
                            )
                            Timber.d("Data: Update $updateItem")
                            viewmodel.updateItem(updateItem)
                        }
                        val action =
                            AddItemFragmentDirections.actionAddItemFragmentToBillFragment(itemId)
                        navController.navigate(action)
                    })
                }

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
                    unit = unit!!,
                    name = product!!.name,
                    discount = discountPrice,
                    quantity = quantity.toInt(),
                    productId = product!!.id!!,
                    itemId = itemId
                )

                item.value?.let {
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
        product = null
        setAllFieldVisible(false)
        etProduct.setText("")
        etQuantity.setText("")
        etDiscount.setText("")
        radioGroupUnit.clearCheck()
        this.subTotal = 0.0f
        this.total = 0.0f
        this.discountPrice = 0.0f
        textInputDiscount.helperText = ""
        invoiceViewModel._price.postValue(
            Price(
                subTotal = 0.0f,
                total = 0.0f
            )
        )
    }

    private fun setEditProduct(lineItemId: Long) {
        viewmodel.getLineItem(lineItemId).observe(viewLifecycleOwner, Observer { it ->
            viewmodel.getProduct(it.productId).observe(viewLifecycleOwner, Observer { product ->
                requireContext().showToast("$product")
                etProduct.setText(it.name, false)
                etQuantity.setText("${it.quantity}")
                textInputDiscount.helperText = "Rs.${it.quantity}"
                val discountPerCent = 100 - ((it.total / it.subTotal) * 100)
                etDiscount.setText("$discountPerCent")
                if (it.unit == radioPrimaryUnit.text.toString()) {
                    radioGroupUnit.check(R.id.radioPrimaryUnit)
                } else {
                    radioGroupUnit.check(R.id.radioSecondaryUnit)
                }
                setAllFieldVisible(true)
                setPrice(product)
                this.product = product
            })

        })


    }

    private fun setProducts(products: List<Product>) {

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

            invoiceViewModel.isPrimaryUnit.observe(viewLifecycleOwner, Observer {
                if (it) {
                    unit = radioPrimaryUnit.text.toString()
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
                    unit = radioSecondaryUnit.text.toString()
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
        if (lineItem != null) {
            save_item_continue.visibility = View.GONE
        }
    }


}
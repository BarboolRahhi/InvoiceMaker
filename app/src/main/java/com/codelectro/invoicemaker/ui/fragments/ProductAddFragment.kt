package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.Product
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.ui.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_add.*
import timber.log.Timber

@AndroidEntryPoint
class ProductAddFragment : Fragment(R.layout.fragment_product_add) {

    private val viewmodel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayPrimary = arrayOf("BAGS-bag", "BOTTLES-btl", "BOX-box", "BUNDLES-bdl",
            "CANS-can", "CARTONS-ctn", "DOZENS-dzn", "GRAMMES-gm", "KILOGRAM-kg",
            "LITER-ltr", "METERS-mtr", "PACKS-pac", "PAIRs-prs", "PIECES-pcs", "QUINTAL-qtl", "ROLLS-rol")

        val arraySecondary = arrayOf("BAGS-bag", "BOTTLES-btl", "BOX-box", "BUNDLES-bdl",
            "CANS-can", "CARTONS-ctn", "DOZENS-dzn", "GRAMMES-gm", "KILOGRAM-kg",
            "LITER-ltr", "METERS-mtr", "PACKS-pac", "PAIRs-prs", "PIECES-pcs", "QUINTAL-qtl", "ROLLS-rol")

        val adapterPrimaary = ArrayAdapter<String>(requireContext(), R.layout.dropdown_menu_popup_item, arrayPrimary)
        etPrimaryUnit.setAdapter(adapterPrimaary)

        val adapterSecondary = ArrayAdapter<String>(requireContext(), R.layout.dropdown_menu_popup_item, arraySecondary)
        etSecondaryUnit.setAdapter(adapterSecondary)

        etPrimaryUnit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textConverter.text = "1 ${etPrimaryUnit.text} ="
            }

        })

        etSecondaryUnit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                primaryText.text = etSecondaryUnit.text
            }

        })


        save_product.setOnClickListener {
            saveProduct()
        }

    }

    private fun saveProduct() {
        val name = etName.text.toString()
        val barCode = etBarCode.text.toString()
        val primaryUnit = etPrimaryUnit.text.toString()
        val secondaryUnit = etSecondaryUnit.text.toString()
        val converterValue = converter_value.text.toString()
        val purchasePrice = etPurchase.text.toString()
        val sellingPrice = etSellingPrice.text.toString()
        val quantity = etQuantity.text.toString()
        if (TextUtils.isEmpty(name)
            || TextUtils.isEmpty(barCode)
            || TextUtils.isEmpty(barCode)
            || TextUtils.isEmpty(primaryUnit)
            || TextUtils.isEmpty(secondaryUnit)
            || TextUtils.isEmpty(converterValue)
            || TextUtils.isEmpty(purchasePrice)
            || TextUtils.isEmpty(sellingPrice)
            || TextUtils.isEmpty(quantity)) {
            requireContext().showToast("All fields are required!")
            return
        }
        val product = Product(
            name = etName.text.toString(),
            barCode = etBarCode.text.toString().toLong(),
            primaryUnit = etPrimaryUnit.text.toString(),
            secondaryUnit = etSecondaryUnit.text.toString(),
            converterValue = converter_value.text.toString().toInt(),
            purchasePrice = etPurchase.text.toString().toFloat(),
            sellingPrice = etSellingPrice.text.toString().toFloat(),
            quantity = etQuantity.text.toString().toInt()
        )
        viewmodel.insertProduct(product)
        findNavController().navigate(R.id.action_productAddFragment_to_productFragment)
        requireContext().showToast("Product Added Successfully!")
    }

}
package com.codelectro.invoicemaker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.adapter.ProductRecyclerViewAdapter
import com.codelectro.invoicemaker.ui.MainActivity
import com.codelectro.invoicemaker.ui.MainViewModel
import com.codelectro.invoicemaker.util.VerticalSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product.*
import timber.log.Timber

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {

    private val viewmodel: MainViewModel by viewModels()

    private lateinit var productAdapter: ProductRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setDrawerEnabled(true)
        productAdapter = ProductRecyclerViewAdapter()

        viewmodel.getProducts().observe(viewLifecycleOwner, Observer {
            Timber.d("products: $it")
            productAdapter.submitList(it)
        })

        add_product.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_productAddFragment)
        }

        setUpRecycleView()
    }

    private fun setUpRecycleView() {
        recyclerview.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(VerticalSpacingItemDecoration(40))
        }
    }

}
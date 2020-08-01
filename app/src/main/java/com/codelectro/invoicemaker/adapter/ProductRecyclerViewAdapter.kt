package com.codelectro.invoicemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.Product
import kotlinx.android.synthetic.main.product_view_item.view.*

class ProductRecyclerViewAdapter :
    ListAdapter<Product, ProductRecyclerViewAdapter.ProductViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductRecyclerViewAdapter.ProductViewholder {
        return ProductViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ProductRecyclerViewAdapter.ProductViewholder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    inner class ProductViewholder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(product: Product) = with(itemView) {
            tvName.text = product.name
            tvPurchasePrice.text = "Purchase Price: Rs${product.purchasePrice}"
            tvSellingPrice.text = "Selling Price: Rs${product.sellingPrice}"
            tvQuantity.text = "Quantity: ${product.quantity}"
            tvStockPrice.text = "Stock Price: Rs${product.quantity * product.purchasePrice}"
            tvConversionUnit.text = "Conversion Unit: 1 ${product.primaryUnit.split("-")[0]} = " +
                    "${product.converterValue} ${product.secondaryUnit.split("-")[0]}"
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Product>()
    {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }


}
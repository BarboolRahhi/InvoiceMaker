package com.codelectro.invoicemaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.Product

class DropDownAdapter(
    context: Context,
    products: List<Product>
) : ArrayAdapter<Product>(context, 0, products) {


    override fun getFilter(): Filter {
        return filter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
           view = LayoutInflater.from(context).inflate(
                R.layout.dropdown_product_item, parent, false
            )
        }
        val tvName= view!!.findViewById<TextView>(R.id.product_name)
        val product = getItem(position)
        product?.let {
            tvName.text = it.name
        }
        return view!!
    }

    internal val filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val suggestions = ArrayList<Product>()

            if (constraint == null || constraint.isEmpty()) {
                suggestions.addAll(products)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                for (product in products) {
                    if (product.name.toLowerCase().contains(filterPattern)) {
                        suggestions.add(product)
                    }
                }

            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            addAll(results?.values as MutableCollection<out Product>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Product).name
        }

    }
}
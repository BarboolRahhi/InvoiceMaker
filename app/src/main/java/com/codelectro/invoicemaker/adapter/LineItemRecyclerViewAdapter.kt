package com.codelectro.invoicemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.LineItem
import kotlinx.android.synthetic.main.bill_view_item.view.*

class LineItemRecyclerViewAdapter :
    ListAdapter<LineItem, LineItemRecyclerViewAdapter.LineItemViewHolder>(DiffCallback()) {

    private lateinit var listener: RecycleViewClickListener<LineItem>

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LineItemRecyclerViewAdapter.LineItemViewHolder {
        return LineItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.bill_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: LineItemRecyclerViewAdapter.LineItemViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<LineItem>() {
        override fun areItemsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
            return oldItem == newItem
        }


    }

    inner class LineItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: LineItem) = with(itemView) {
            tvName.text = item.name.toUpperCase()
            tvTotal.text = "Rs.${item.total}"
            tvQuantity.text = "${item.quantity} x Rs.${item.subTotal / item.quantity}"
            deleteBtn.setOnClickListener {
                listener.onDeleteClick(it, item)
            }
            setOnClickListener {
                listener.onClick(it, item)
            }
        }
    }

    fun setOnClickItemListener(listener: RecycleViewClickListener<LineItem>) {
        this.listener = listener
    }
}

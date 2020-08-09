package com.codelectro.invoicemaker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.entity.UserAndItem
import com.codelectro.invoicemaker.ui.dateTimeFormat
import kotlinx.android.synthetic.main.invoice_view_item.view.*
import kotlinx.android.synthetic.main.product_view_item.view.tvName
import java.text.SimpleDateFormat
import java.util.*

class InvoiceRecyclerViewAdapter :
    ListAdapter<UserAndItem, InvoiceRecyclerViewAdapter.UserAndItemViewholder>(DiffCallback()) {

    private lateinit var clickListener: InvoiceClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvoiceRecyclerViewAdapter.UserAndItemViewholder {
        return UserAndItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.invoice_view_item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: InvoiceRecyclerViewAdapter.UserAndItemViewholder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }


    inner class UserAndItemViewholder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(userAndItem: UserAndItem) = with(itemView) {
           userAndItem.apply {
               tvName.text = user?.customerName?.capitalize()
               tvTotal.text = "Rs.${item.total}"
               tvPaid.text = "Rs.${item.paid}"
               tvBalance.text = "Rs.${item.balance}"
               tvDate.text = "Issued on ${item.date.dateTimeFormat()}"
               tvInvoice.text = "INV-${String.format("%03d" ,item.invoiceNumber)}"
               menuBtn.setOnClickListener {
                   clickListener.onMenuClick(it, this)
               }
           }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserAndItem>()
    {
        override fun areItemsTheSame(oldItem: UserAndItem, newItem: UserAndItem): Boolean {
            return oldItem.user?.id == newItem.user?.id
        }

        override fun areContentsTheSame(oldItem: UserAndItem, newItem: UserAndItem): Boolean {
            return oldItem == newItem
        }

    }

    fun setOnInvoiceClickListener(clickListener: InvoiceClickListener) {
        this.clickListener = clickListener
    }

    interface InvoiceClickListener {
        fun onMenuClick(view: View, userAndItem: UserAndItem)
    }

}
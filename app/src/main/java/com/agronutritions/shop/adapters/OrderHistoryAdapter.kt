package com.agronutritions.shop.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.OrderHistoryDetails
import com.agronutritions.shop.utils.Utils

class OrderHistoryAdapter  internal constructor(private val context: Context, private val mData: ArrayList<OrderHistoryDetails>, val listner:Invoicecommunicator): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var customer_id: TextView = itemView.findViewById(R.id.customer_id)
        internal var order_id: TextView = itemView.findViewById(R.id.order_id)
        internal var order_date: TextView = itemView.findViewById(R.id.order_date)
        internal var delivered_date: TextView = itemView.findViewById(R.id.delivered_date)
        internal var product_name: TextView = itemView.findViewById(R.id.product_name)
        internal var amount: TextView = itemView.findViewById(R.id.amount)
        internal var order_status: TextView = itemView.findViewById(R.id.order_status)
        internal var view_invoice: TextView = itemView.findViewById(R.id.view_invoice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.order_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.customer_id.text = data.customer_id
        holder.order_id.text = data.order_id
        if (Utils.formatDate_yyyyMMddHHmmss(data.order_date)!!.isNotEmpty()) {
            holder.order_date.text = Utils.formatDate_yyyyMMddHHmmss(data.order_date)
        } else {
            holder.order_date.text = "--"
        }
        if (Utils.formatDate_yyyyMMddHHmmss(data.delivered_date)!!.isNotEmpty()) {
            holder.delivered_date.text =Utils.formatDate_yyyyMMddHHmmss(data.delivered_date)
        } else {
            holder.delivered_date.text = "--"
        }
        holder.product_name.text = data.product_name
        holder.amount.text = context.resources.getString(R.string.Rs) + " " + data.amount

        if(TextUtils.equals(data.order_status,"1")) {
            holder.order_status.text = "Ordered"
        }
        else if(TextUtils.equals(data.order_status,"2")) {
            holder.order_status.text = "Shipped"
        }
        else if(TextUtils.equals(data.order_status,"3")) {
            holder.order_status.text = "Delivered"
        }
        holder.view_invoice.setOnClickListener {
            listner.onRowClick(data)

        }

    }
    interface Invoicecommunicator {
        fun onRowClick(data: OrderHistoryDetails)
    }

}
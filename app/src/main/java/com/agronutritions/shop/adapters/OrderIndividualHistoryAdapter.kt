package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.OrderTransHistoryDetails

class OrderIndividualHistoryAdapter  internal constructor(private val context: Context, private val mData: ArrayList<OrderTransHistoryDetails>) : RecyclerView.Adapter<OrderIndividualHistoryAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var prName: TextView = itemView.findViewById(R.id.prName)
        internal var prPrice: TextView = itemView.findViewById(R.id.prPrice)
        internal var prQty: TextView = itemView.findViewById(R.id.prQty)
        internal var prTotal: TextView = itemView.findViewById(R.id.prTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.order_individual_history_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.prName.text = data.ord_trans_id
        holder.prPrice.text = "Purchase Price: " + context.resources.getString(R.string.Rs) + data.ord_trans_unit_price
        holder.prQty.text = "Purchase Quantity: " + data.ord_trans_qty

        var total = 0.0
        try {
            total = data.ord_trans_unit_price.toDouble() * data.ord_trans_qty.toDouble()
        } catch (e: Exception) {
        }

        holder.prTotal.text =  "Total\n" + context.resources.getString(R.string.Rs) + total.toString()
    }

}
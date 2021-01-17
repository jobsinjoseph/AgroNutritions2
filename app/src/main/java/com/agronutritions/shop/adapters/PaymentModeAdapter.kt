package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.PaymentModeDetails

class PaymentModeAdapter   internal constructor(private val context: Context, private val mData: ArrayList<PaymentModeDetails>, val clickHandler: (view: View, pin: String, pinId: String) -> Unit) : RecyclerView.Adapter<PaymentModeAdapter.ViewHolder>()   {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var pincodeLay: LinearLayout = itemView.findViewById(R.id.pincodeLay)
        internal var mode: TextView = itemView.findViewById(R.id.mode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.payment_mode_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.mode.text = data.paymentMode

        holder.pincodeLay.setOnClickListener(View.OnClickListener {
            clickHandler(it, data.paymentMode, data.paymentModeId)
        })
    }
}
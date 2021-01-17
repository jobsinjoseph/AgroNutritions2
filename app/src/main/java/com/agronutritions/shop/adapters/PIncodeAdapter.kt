package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.PinDetails

class PIncodeAdapter   internal constructor(private val context: Context, private val mData: ArrayList<PinDetails>, val clickHandler: (view: View, pin: String, pinId: String) -> Unit) : RecyclerView.Adapter<PIncodeAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var pincodeLay: LinearLayout = itemView.findViewById(R.id.pincodeLay)
        internal var code: TextView = itemView.findViewById(R.id.code)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.pin_code_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.code.text = data.pin_num

        holder.pincodeLay.setOnClickListener(View.OnClickListener {
            clickHandler(it, data.pin_num, data.pin_id)
        })
    }
}
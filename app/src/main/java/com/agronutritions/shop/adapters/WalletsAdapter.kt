package com.agronutritions.shop.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.ReportMenuDetails
import com.agronutritions.shop.api.response.WalletDetails

class WalletsAdapter  internal constructor(private val context: Context, private val mData: ArrayList<WalletDetails>, val clickHandler: (view: View, ReportMenu: ReportMenuDetails) -> Unit) : RecyclerView.Adapter<WalletsAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var customer_id: TextView = itemView.findViewById(R.id.customer_id)
        internal var order_id: TextView = itemView.findViewById(R.id.order_id)
        internal var phone_number: TextView = itemView.findViewById(R.id.phone_number)
        internal var ref_id: TextView = itemView.findViewById(R.id.ref_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.wallets_data_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.customer_id.text = data.customer_id
        holder.order_id.text = data.order_id
        holder.phone_number.text = context.getString(R.string.Rs) + " " + data.walletamt
        holder.ref_id.text = data.percentage + "%"
        /*holder.itemView.setOnClickListener {
            clickHandler(it, data)
        }*/


    }


}
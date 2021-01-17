package com.agronutritions.shop.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.GenologyDetails
import com.agronutritions.shop.api.response.ReportMenuDetails

class GeneologysMenuAdapter  internal constructor(private val context: Context, private val mData: ArrayList<GenologyDetails>, val clickHandler: (view: View, ReportMenu: ReportMenuDetails) -> Unit) : RecyclerView.Adapter<GeneologysMenuAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView = itemView.findViewById(R.id.name)
        internal var phone_number: TextView = itemView.findViewById(R.id.phone_number)
        internal var ref_id: TextView = itemView.findViewById(R.id.ref_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.geneo_report_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.name.text = data.name
        holder.phone_number.text = data.phone_number
        holder.ref_id.text = data.ref_id
        /*holder.itemView.setOnClickListener {
            clickHandler(it, data)
        }*/


    }


}
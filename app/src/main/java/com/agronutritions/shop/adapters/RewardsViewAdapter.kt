package com.agronutritions.shop.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.ReportMenuDetails
import com.agronutritions.shop.api.response.RewardsDetails

class RewardsViewAdapter  internal constructor(private val context: Context, private val mData: ArrayList<RewardsDetails>, val clickHandler: (view: View, ReportMenu: ReportMenuDetails) -> Unit) : RecyclerView.Adapter<RewardsViewAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cid: TextView = itemView.findViewById(R.id.cid)
        internal var description: TextView = itemView.findViewById(R.id.description)
        internal var first_level_min: TextView = itemView.findViewById(R.id.first_level_min)
        internal var second_level_min: TextView = itemView.findViewById(R.id.second_level_min)
        internal var gift: TextView = itemView.findViewById(R.id.gift)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.rewards_data_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.cid.text = data.id
        holder.description.text = data.description
        holder.first_level_min.text =data.first_level_min
        holder.second_level_min.text = data.second_level_min
        holder.gift.text = data.gift
        /*holder.itemView.setOnClickListener {
            clickHandler(it, data)
        }*/


    }


}
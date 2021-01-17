package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.RecentListDetails

class RecentListAdapter internal constructor(private val context: Context, private val mData: List<RecentListDetails>): RecyclerView.Adapter<RecentListAdapter.ViewHolder>() {

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var card: CardView = itemView.findViewById(R.id.card)
        internal var banner: ImageView = itemView.findViewById(R.id.banner)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recent_order_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.card.setCardBackgroundColor(mData[position].color_id)
    }
}
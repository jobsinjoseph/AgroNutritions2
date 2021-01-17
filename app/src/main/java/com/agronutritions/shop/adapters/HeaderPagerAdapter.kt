package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.PagerDetails
import com.agronutritions.shop.constants.AppConstants

class HeaderPagerAdapter internal constructor(private val context: Context, private val mData: List<PagerDetails>): RecyclerView.Adapter<HeaderPagerAdapter.ViewHolder>() {

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var banner: ImageView = itemView.findViewById(R.id.banner)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.header_pager_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        Glide.with(context)
            .load(AppConstants.BANNER_URL + data.banner_img)
            .placeholder(R.drawable.cat_img)
            .into(holder.banner)
    }
}
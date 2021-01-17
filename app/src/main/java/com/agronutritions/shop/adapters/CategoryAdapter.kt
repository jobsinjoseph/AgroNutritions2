package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.CategoryDetails
import com.agronutritions.shop.constants.AppConstants
import com.makeramen.roundedimageview.RoundedImageView

class CategoryAdapter internal constructor(private val context: Context, private val mData: List<CategoryDetails>,val listner:Categorycommunicator): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var catImg: RoundedImageView = itemView.findViewById(R.id.catImg)
        internal var catName: TextView = itemView.findViewById(R.id.catName)

    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.catName.text = data.cat_name

        Glide.with(context)
            .load(AppConstants.IMAGE_URL + data.cat_img)
            //.apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.cat_img)
            .into(holder.catImg)
        holder.itemView.setOnClickListener{
            listner.onRowClick(data)
        }
    }
    interface Categorycommunicator {
        fun onRowClick(data:CategoryDetails)
    }
}
package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.SubCategoryDetails
import org.jetbrains.anko.textColor

class SubCategoryAdapter internal constructor(private val context: Context, private val mData: List<SubCategoryDetails>, val listner:SubCategorycommunicator): RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {
    var selectedPosition : Int = 0

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var catImg: ImageView = itemView.findViewById(R.id.catImg)
        internal var catName: TextView = itemView.findViewById(R.id.catName)
        internal var boundary: View = itemView.findViewById(R.id.boundary)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.category_sub_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.catName.text = data.cat_name
        if (selectedPosition == position) {
            holder.boundary.visibility = View.VISIBLE
            holder.catName.textColor = context.resources.getColor(R.color.colorWhite)
        } else {
            holder.catName.textColor = context.resources.getColor(R.color.colorOffBlack)
            holder.boundary.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            selectedPosition = position
            listner.onRowClick(data)
            notifyDataSetChanged()
        }
    }
    interface SubCategorycommunicator {
        fun onRowClick(data:SubCategoryDetails)
    }
}
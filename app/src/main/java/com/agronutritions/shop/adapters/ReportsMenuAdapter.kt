package com.agronutritions.shop.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.ReportMenuDetails
import com.agronutritions.shop.constants.AppConstants
import com.bumptech.glide.Glide

class ReportsMenuAdapter  internal constructor(private val context: Context, private val mData: ArrayList<ReportMenuDetails>, val clickHandler: (view: View, ReportMenu: ReportMenuDetails) -> Unit) : RecyclerView.Adapter<ReportsMenuAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var menus: TextView = itemView.findViewById(R.id.menus)
        internal var ico: ImageView = itemView.findViewById(R.id.ico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.report_menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.menus.text = data.menu_name

        if (data.pic != null && data.pic.isNotEmpty()){
            Glide.with(context)
                .load(AppConstants.BANNER_URL + data.pic)
                .placeholder(R.drawable.logo_rpay)
                .into(holder.ico)
        }

        holder.itemView.setOnClickListener {
            clickHandler(it, data)
        }


    }


}
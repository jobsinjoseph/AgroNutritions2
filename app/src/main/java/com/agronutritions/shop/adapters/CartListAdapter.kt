package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.agronutritions.shop.R
import com.agronutritions.shop.app.Session.Companion.orderPrdtList
import com.agronutritions.shop.constants.AppConstants
import com.agronutritions.shop.model.OrderProductData

class CartListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<OrderProductData>, val clickHandler: (view: View, orderProductData: OrderProductData?) -> Unit) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var pdtName: TextView = itemView.findViewById(R.id.pdtName)
        internal var qtyAdd: ImageView = itemView.findViewById(R.id.qtyAdd)
        internal var qty: TextView = itemView.findViewById(R.id.qty)
        internal var qtyRemove: ImageView = itemView.findViewById(R.id.qtyRemove)
        internal var pdtPrice: TextView = itemView.findViewById(R.id.pdtPrice)
        internal var pdtTotalPrice: TextView = itemView.findViewById(R.id.pdtTotalPrice)

        internal var product_Img: ImageView = itemView.findViewById(R.id.product_Img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.cart_items_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.pdtName.text = data.pdt_name
        holder.pdtPrice.text = context.resources.getString(R.string.Rs) + data.pdt_price
        holder.pdtTotalPrice.text = context.resources.getString(R.string.Rs) + data.pdt_total_price
        holder.qty.text = data.pdt_qty

        holder.qtyAdd.setOnClickListener {
            val updatedQty = updateQuantity(holder.qty, true)
            holder.qty.text = updatedQty
            updateOrderData(data, updatedQty.toInt())
            clickHandler(it, getOrderProduct(data, updatedQty.toInt()))
            notifyDataSetChanged()
        }

        holder.qtyRemove.setOnClickListener(View.OnClickListener {
            if (holder.qty.text.toString().toInt() > 1) {
                val updatedQty = updateQuantity(holder.qty, false)
                holder.qty.text = updatedQty
                updateOrderData(data, updatedQty.toInt())

                clickHandler(it, getOrderProduct(data, updatedQty.toInt()))
                notifyDataSetChanged()
            } else {
                updateOrderData(data, 0)
                clickHandler(it, getOrderProduct(data, 0))
                notifyDataSetChanged()
            }
        })


        ///
        Glide.with(context)
            .load(AppConstants.IMAGE_URL + data.pdt_img)
            .placeholder(R.drawable.cat_img)
            .into(holder.product_Img)

    }

    private fun updateQuantity(qtyText: TextView, isAdd : Boolean) : String{
        var qty : Int = qtyText.text.toString().toInt()
        if (isAdd){
            qty += 1
        }else{
            qty -= 1
        }

        return qty.toString()
    }

    private fun updateOrderData(data: OrderProductData, qty: Int){

        for (i in 0 until orderPrdtList.size){
            if (orderPrdtList[i].pdt_id == data.pdt_id){
                if (qty>0.0)  {
                    val totalPrice = data.pdt_price.toDouble() * qty
                    val prdtTotalPrice = String.format("%.2f", totalPrice)
                    orderPrdtList[i].pdt_qty = qty.toString()
                    orderPrdtList[i].pdt_total_price = prdtTotalPrice
                } else {
                    orderPrdtList.removeAt(i)
                }
                break
            }
        }
    }

    private fun getOrderProduct(data: OrderProductData, qty: Int) : OrderProductData{
        val totalPrice = data.pdt_price.toDouble() * qty
        val prdtTotalPrice = String.format("%.2f", totalPrice)
        return OrderProductData(data.pdt_id, data.pdt_name, data.pdt_cat_id, data.pdt_price, qty.toString(), prdtTotalPrice,data.pdt_img)
    }
}
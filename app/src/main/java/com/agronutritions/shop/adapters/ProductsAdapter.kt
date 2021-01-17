package com.agronutritions.shop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.ProductDetails
import com.agronutritions.shop.app.Session.Companion.orderPrdtList
import com.agronutritions.shop.constants.AppConstants
import com.agronutritions.shop.model.OrderProductData
import com.makeramen.roundedimageview.RoundedImageView

class ProductsAdapter internal constructor(private val context: Context, private val mData: List<ProductDetails>,val listner:Productcommunicator,val clickHandler: (view: View, count: String, orderProductData: OrderProductData?) -> Unit) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {


    private val mInflater: LayoutInflater = LayoutInflater.from(context)


    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvPdtName: TextView = itemView.findViewById(R.id.tvPdtName)
        internal var pimg: RoundedImageView = itemView.findViewById(R.id.pimg)
        internal var addBtn: ImageView = itemView.findViewById(R.id.addBtn)
        internal var qtyLay: LinearLayout = itemView.findViewById(R.id.qtyLay)
        internal var qtyAdd: ImageView = itemView.findViewById(R.id.qtyAdd)
        internal var qty: TextView = itemView.findViewById(R.id.qty)
        internal var qtyRemove: ImageView = itemView.findViewById(R.id.qtyRemove)
        internal var pdtPrice: TextView = itemView.findViewById(R.id.pdtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.product_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]
        holder.tvPdtName.text = data.pdt_name

        holder.pdtPrice.text = context.resources.getString(R.string.Rs)+" "+ data.pdt_price

        Glide.with(context)
            .load(AppConstants.IMAGE_URL + data.pdt_img)
            .placeholder(R.drawable.cat_img)
            .into(holder.pimg)

        if (orderPrdtList.isNotEmpty()){
            for (i in 0 until orderPrdtList.size){
                if (orderPrdtList[i].pdt_id == data.pdt_id){
                    holder.addBtn.visibility = View.VISIBLE
                    holder.qtyLay.visibility = View.GONE
                    holder.qty.setText(orderPrdtList[i].pdt_qty)
                    break
                }else{
                    holder.addBtn.visibility = View.VISIBLE
                    holder.qtyLay.visibility = View.GONE
                }
            }
        }else{
            holder.addBtn.visibility = View.VISIBLE
            holder.qtyLay.visibility = View.GONE
        }

        holder.addBtn.setOnClickListener(View.OnClickListener {
            holder.addBtn.visibility = View.VISIBLE
            //holder.qtyLay.visibility = View.VISIBLE
            updateOrderData(data, 1)
            clickHandler(it, orderPrdtList.size.toString(), getOrderProduct(data, 1))
            Toast.makeText(context,"Item Added to Cart Successfully",Toast.LENGTH_LONG).show()
        })

        holder.qtyAdd.setOnClickListener(View.OnClickListener {
            val updatedQty = updateQuantity(holder.qty, true)
            holder.qty.text = updatedQty
            updateOrderData(data, updatedQty.toInt())
            clickHandler(it, orderPrdtList.size.toString(), getOrderProduct(data, updatedQty.toInt()))
        })

        holder.qtyRemove.setOnClickListener(View.OnClickListener {
            if (holder.qty.text.toString().toInt() > 1) {
                val updatedQty = updateQuantity(holder.qty, false)
                holder.qty.text = updatedQty
                updateOrderData(data, updatedQty.toInt())
                clickHandler(it, orderPrdtList.size.toString(), getOrderProduct(data, updatedQty.toInt()))
            } else {
                holder.addBtn.visibility = View.VISIBLE
                holder.qtyLay.visibility = View.GONE
                updateOrderData(data, 0)
                clickHandler(it, orderPrdtList.size.toString(), getOrderProduct(data, 0))
            }
        })

        //holder.addBtn.setColorFilter(ContextCompat.getColor(context, R.color.cart_icon_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        //holder.addBtn.setColorFilter(ContextCompat.getColor(context, R.color.cart_icon_color), android.graphics.PorterDuff.Mode.SRC_IN);

        holder.itemView.setOnClickListener {
            listner.onRowClick(data)

        }
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

    private fun updateOrderData(data: ProductDetails, qty: Int){

        for (i in 0 until orderPrdtList.size){
            if (orderPrdtList[i].pdt_id == data.pdt_id){
                orderPrdtList.removeAt(i)
                break
            }
        }

        if (qty>0) {
            orderPrdtList.add(getOrderProduct(data, qty))
        }
    }

    private fun getOrderProduct(data: ProductDetails, qty: Int) : OrderProductData{
        val totalPrice = data.pdt_price.toDouble() * qty
        val prdtTotalPrice = String.format("%.2f", totalPrice)
        return OrderProductData(data.pdt_id, data.pdt_name, data.pdt_cat_id, data.pdt_price, qty.toString(), prdtTotalPrice,data.pdt_img)
    }

    interface Productcommunicator {
        fun onRowClick(data: ProductDetails)
    }
}
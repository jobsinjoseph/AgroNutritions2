package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.ProductData
import com.agronutritions.shop.api.response.ProductDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.constants.AppConstants
import com.agronutritions.shop.model.OrderProductData
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_product_list_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class ProductDetailsFragment : BaseFragment() {
    var productid = ""
    var productList: ArrayList<ProductDetails> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_list_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as Home).manageBottomBar(true)
        initiateViews()
    }

    private fun initiateViews(){
        productid = arguments!!.getString("productid")!!;
        getProducts(productid)
    }
    private fun getProducts(pId: String) {
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProductData>> {
                    getProductDetails(productJsonRequest(pId))
                }
                if (response != null){
                    productList = response.body()?.data!!
                    productList[0]
                    showProductData(productList[0])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()

        }
    }


    private fun productJsonRequest(P_ID: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("productid",P_ID)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

   private fun showProductData(productDetails: ProductDetails)
    {

        Glide.with(this!!.activity!!)
            .load(AppConstants.IMAGE_URL + productDetails.pdt_img)
            .placeholder(R.drawable.cat_img)
            .into(pimg)
        pdtPrice.text=activity!!.resources.getString(R.string.Rs)+" "+ productDetails.pdt_price
        tv_description.text=productDetails.pdt_description


        //////
        addBtn.setOnClickListener(View.OnClickListener {
            addBtn.visibility = View.GONE
            qtyLay.visibility = View.GONE
            updateOrderData(productDetails, 1)
            (activity as Home).updateCountFlag(Session.orderPrdtList.size.toString())
            Toast.makeText(context,"Item Added to Cart Successfully", Toast.LENGTH_LONG).show()
        })

        qtyAdd.setOnClickListener(View.OnClickListener {
            val updatedQty = updateQuantity(qty, true)
            qty.text = updatedQty
            updateOrderData(productDetails, updatedQty.toInt())
            //clickHandler(it, Session.orderPrdtList.size.toString(), getOrderProduct(data, updatedQty.toInt()))
        })

        qtyRemove.setOnClickListener(View.OnClickListener {
            if (qty.text.toString().toInt() > 1) {
                val updatedQty = updateQuantity(qty, false)
                qty.text = updatedQty
                updateOrderData(productDetails, updatedQty.toInt())
                //clickHandler(it, Session.orderPrdtList.size.toString(), getOrderProduct(data, updatedQty.toInt()))
            } else {
                addBtn.visibility = View.VISIBLE
                qtyLay.visibility = View.GONE
                updateOrderData(productDetails, 0)
                //clickHandler(it, Session.orderPrdtList.size.toString(), getOrderProduct(data, 0))
            }
        })

        //////////
        if (Session.orderPrdtList.isNotEmpty()){
            for (i in 0 until Session.orderPrdtList.size){
                if (Session.orderPrdtList[i].pdt_id == productDetails.pdt_id){
                    addBtn.visibility = View.GONE
                    qtyLay.visibility = View.GONE
                    qty.text = Session.orderPrdtList[i].pdt_qty
                    break
                }else{
                    addBtn.visibility = View.VISIBLE
                    qtyLay.visibility = View.GONE
                }
            }
        }else{
            addBtn.visibility = View.VISIBLE
            qtyLay.visibility = View.GONE
        }


    }

    private fun updateOrderData(data: ProductDetails, qty: Int){

        for (i in 0 until Session.orderPrdtList.size){
            if (Session.orderPrdtList[i].pdt_id == data.pdt_id){
                Session.orderPrdtList.removeAt(i)
                break
            }
        }

        if (qty>0) {
            Session.orderPrdtList.add(getOrderProduct(data, qty))
        }
    }
    private fun getOrderProduct(data: ProductDetails, qty: Int) : OrderProductData {
        val totalPrice = data.pdt_price.toDouble() * qty
        val prdtTotalPrice = String.format("%.2f", totalPrice)
        return OrderProductData(data.pdt_id, data.pdt_name, data.pdt_cat_id, data.pdt_price, qty.toString(), prdtTotalPrice,data.pdt_img)
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

}
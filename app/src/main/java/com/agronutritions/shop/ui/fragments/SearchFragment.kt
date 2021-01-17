package com.agronutritions.shop.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.agronutritions.shop.R
import com.agronutritions.shop.api.response.ProductDetails
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.model.OrderProductData
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : BaseFragment(){
    lateinit var adapter: ArrayAdapter<ProductDetails>
    private lateinit var product: ProductDetails

    private fun initViews(){
        adapter= ArrayAdapter<ProductDetails>(activity!!, R.layout.layout_text_autocomplete, Session.totalProductList)
        search_auto.threshold = 1;
        search_auto.setAdapter(adapter);
        search_auto.setTextColor(ContextCompat.getColor(context!!, R.color.colorOffBlack))

        val imgr: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        search_auto.requestFocus();
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

        for (i in 0 until Session.orderPrdtList.size){
            if (Session.orderPrdtList[i].pdt_id == data.pdt_id){
                Session.orderPrdtList.removeAt(i)
                break
            }
        }

        if (qty>0) {
            val totalPrice = data.pdt_price.toDouble() * qty
            val prdtTotalPrice = String.format("%.2f", totalPrice)
            val orderProduct = OrderProductData(data.pdt_id, data.pdt_name, data.pdt_cat_id, data.pdt_price, qty.toString(), prdtTotalPrice,data.pdt_img)

            Session.orderPrdtList.add(orderProduct)
        }
    }

    private fun setProduct(data: ProductDetails){
        /*tvPdtName.text = data.pdt_name
        pdtPrice.text = resources.getString(R.string.Rs) + data.pdt_price

        Glide.with(activity!!)
            .load(AppConstants.IMAGE_URL + data.pdt_img)
            .placeholder(R.drawable.cat_img)
            .into(pimg)*/
        val imgr: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(view?.windowToken, 0)

        val bundle = Bundle()
        bundle.putString("productid", data.pdt_id)

        /*(activity as Home).setFragment(ProductDetailsFragment(),
            FragmentConstants.PRODUCT_DETAILS_FRAGMENT, bundle, false)*/
        (activity as Home).onProductdetailsNav(bundle,data.pdt_name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(false)
        initViews()

        search_auto.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                product = adapter.getItem(position)!!
                setProduct(product!!)
                searchResult.visibility = View.VISIBLE
            }

        search_auto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchResult.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable) {}
        })


        addBtn.setOnClickListener(View.OnClickListener {
            addBtn.visibility = View.GONE
            qtyLay.visibility = View.VISIBLE
            updateOrderData(product, 1)
        })

        qtyAdd.setOnClickListener(View.OnClickListener {
            qty.text = updateQuantity(qty, true)
            updateOrderData(product, qty.text.toString().toInt())
        })

        qtyRemove.setOnClickListener(View.OnClickListener {
            if (qty.text.toString().toInt() > 1) {
                qty.text = updateQuantity(qty, false)
                updateOrderData(product, qty.text.toString().toInt())
            } else {
                addBtn.visibility = View.VISIBLE
                qtyLay.visibility = View.GONE
                updateOrderData(product, 0)
            }
        })
    }
}
package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.*
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.app.Session.Companion.orderPrdtList
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment1 : BaseFragment(){

    private lateinit var headerPagerAdapter: HeaderPagerAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var pIncodeAdapter: PIncodeAdapter

    var headerPagerList: ArrayList<PagerDetails> = ArrayList()
    var categoriesList: ArrayList<CategoryDetails> = ArrayList()
    //var products: ArrayList<ProductDetails> = ArrayList()
    private lateinit var timer : Timer


    private fun initiateViews(){
        headerPager.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        categoryList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        //ProductList.layoutManager = LinearLayoutManager(activity)

        if (orderPrdtList.isNotEmpty()){
            (activity as Home).updateCountFlag(orderPrdtList.size.toString())
        }

        if (Session.headerBannerItemsList.isEmpty()) {
            getBanners()
        } else {
            headerPagerList = Session.headerBannerItemsList
            showHeaderBanner()
        }


        if (Session.pinCodeItemsList.isEmpty()) {
            //getPincodeList()
        } else {
            if (Session.mainCategoriesList.isEmpty()) {
                getCategories()
            } else {
                categoriesList = Session.mainCategoriesList
                //showCategoriesList()
            }
        }
    }

    private fun showHeaderBanner(){
        headerPagerAdapter = HeaderPagerAdapter(activity!!, headerPagerList)
        headerPager.adapter = headerPagerAdapter
        setupHeaderAutoScroll()
        if (Session.pinCodeItemsList.isEmpty()) {
           // getPincodeList()
        } else {
            if (Session.mainCategoriesList.isEmpty()) {
                getCategories()
            } else {
                categoriesList = Session.mainCategoriesList
                //showCategoriesList()
            }
        }
    }

    /*private fun showCategoriesList(){
        categoryAdapter = CategoryAdapter(activity!!, categoriesList){ _, category ->
            //getProducts(category.cat_id)
        }
        categoryList.adapter = categoryAdapter
        if (Session.mainProductList.isEmpty()) {
            //getProducts(categoriesList[0].cat_id)
        } else {
            //products = Session.mainProductList
           // showProducts()
        }
    }*/

    /*private fun showProducts(){
        productsAdapter = ProductsAdapter(activity!!, products){_, count, orderProduct ->
            (activity as Home).updateCountFlag(count)
            if (orderProduct != null && orderProduct.pdt_qty.toDouble()>0) {
                (activity as Home).dbAddProductToCart(orderProduct!!)
            }else if (orderProduct != null && orderProduct.pdt_qty.toDouble()==0.0){
                (activity as Home).dbRemoveProductFromCart(orderProduct!!.pdt_id)
            }
        }
        ProductList.adapter = productsAdapter
    }*/

    private fun getBanners(){
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<PagerData>> {
                    getBannerList()
                }
                if (response != null){
                    headerPagerList = response.body()?.data!!
                    Session.headerBannerItemsList = headerPagerList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()
            showHeaderBanner()
        }
    }

    private fun getPincodeList(){
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<PinData>> {
                    getPinCodeList(categoryJsonRequest())
                }
                if (response != null){
                    Session.pinCodeItemsList = response.body()?.data!!.pin_list
//                    Session.userLocationSub = response.body()?.data!!.pin_list[0].pin_num
//                    Session.pincodeId = response.body()?.data!!.pin_list[0].pin_id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()
            getCategories()
        }
    }

    private fun getCategories(){
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<CategoryData>> {
                    getCategoryList()
                }
                if (response != null){
                    categoriesList = response.body()?.cat_list!!
                    Session.mainCategoriesList = categoriesList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()
            //showCategoriesList()
        }
    }

   /* private fun getProducts(cat_id : String){
        lifecycleScope.launch {
            showProgress(activity, "Fetching Products..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProductData>> {
                    getProductsList(productJsonRequest(cat_id))
                }
                if (response != null){
                    products = response.body()?.data!!
                    Session.mainProductList = products
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()
            showProducts()
        }
    }*/

    private fun categoryJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("secure_key", activity!!.resources.getString(R.string.api_key))
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun productJsonRequest(cat_id : String) : RequestBody{

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("secure_key", activity!!.resources.getString(R.string.api_key))
            json.put("cat_id", cat_id)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun setupHeaderAutoScroll(){
        timer = Timer("schedule", true)
        var position = 0
        var ends = false
        timer.scheduleAtFixedRate(2000, 2000) {
            when {
                position === headerPagerList.size - 1 -> {
                    ends = true
                }
                position === 0 -> {
                    ends = false
                }
            }
            if (!ends) {
                position++
            } else {
                position = 0
            }
            headerPager.smoothScrollToPosition(position)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as Home).manageBottomBar(true)
        initiateViews()

        promptSearch.setOnClickListener {
            (activity as Home).openSearchFragment()
        }
    }
}
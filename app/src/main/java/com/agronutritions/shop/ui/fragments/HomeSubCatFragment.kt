package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.ProductsAdapter
import com.agronutritions.shop.adapters.SubCategoryAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.fragment_sub_home.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class HomeSubCatFragment : BaseFragment(), SubCategoryAdapter.SubCategorycommunicator,ProductsAdapter.Productcommunicator {
    var cat_Id = ""
    var subcategoriesList: ArrayList<SubCategoryDetails> = ArrayList()
    var productList: ArrayList<ProductDetails> = ArrayList()
    var subcatadapter: SubCategoryAdapter?=null
    private fun initiateViews(){
        arguments?.let {
            if (it.containsKey("cat_id")){
                cat_Id = it["cat_id"].toString()
            }else{
                cat_Id = Session.mainCategoriesList[0].cat_id
                (activity as Home).setToolBarTitle(Session.mainCategoriesList[0].cat_name)
            }
        }?: run {
            cat_Id = Session.mainCategoriesList[0].cat_id
            (activity as Home).setToolBarTitle(Session.mainCategoriesList[0].cat_name)
        }

        rv_sub_category.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        if (Session.mainsubCategoriesList.isEmpty()) {
            getSubCategories(cat_Id)
            } else {
            subcategoriesList = Session.mainsubCategoriesList
                showSubCategoriesList()
            }

        //product
        if (Session.orderPrdtList.isNotEmpty()){
            (activity as Home).updateCountFlag(Session.orderPrdtList.size.toString())
        }
        else
        {
            (activity as Home).updateCountFlag(Session.orderPrdtList.size.toString())
        }

    }
    private fun showSubCategoriesList(){
        subcatadapter= SubCategoryAdapter(activity!!, subcategoriesList,this)
        rv_sub_category.adapter = subcatadapter

        if (Session.mainProductList.isEmpty()) {
            getProducts(subcategoriesList[0].cat_id)
        } else {
            productList = Session.mainProductList
            showProductList()
        }

    }


    private fun showProductList(){
        rv_products.setOrientation(DSVOrientation.HORIZONTAL)
        var  infiniteAdapter = InfiniteScrollAdapter.wrap( ProductsAdapter(activity!!, productList,this){_, count, orderProduct ->
            (activity as Home).updateCountFlag(count)
            if (orderProduct != null && orderProduct.pdt_qty.toDouble()>0) {
                (activity as Home).dbAddProductToCart(orderProduct!!)
            }else if (orderProduct != null && orderProduct.pdt_qty.toDouble()==0.0){
                (activity as Home).dbRemoveProductFromCart(orderProduct!!.pdt_id)
            }
        });
        rv_products.adapter = infiniteAdapter
        //rv_products.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime())
        rv_products.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )

    }


    private fun getSubCategories(catId: String) {
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<SubCategoryData>> {
                    getsubCategoryList(subcategoryJsonRequest(catId))
                }
                if (response != null){
                    subcategoriesList = response.body()?.cat_list!!
                    Session.mainsubCategoriesList = subcategoriesList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismissProgress()
            showSubCategoriesList()

        }
    }

    private fun subcategoryJsonRequest(cat_ID: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("catid",cat_ID)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
    private fun productJsonRequest(sub_cat_ID: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("subcatid",sub_cat_ID)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
    private fun getProducts(sub_catId: String) {
        lifecycleScope.launch {
            //showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProductData>> {
                    getProductsList(productJsonRequest(sub_catId))
                }
                if (response != null){
                    productList = response.body()?.data!!
                    Session.mainProductList = productList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //dismissProgress()
            showProductList()
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
                    Session.mainCategoriesList = response.body()?.cat_list!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgress()
            }
            dismissProgress()
            if (Session.totalProductList.isEmpty()) {
                getAllProducts()
            }else{
                initiateViews()
            }
        }
    }

    private fun getAllProducts(){
        lifecycleScope.launch {
            showProgress(activity, "Fetching data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProductData>> {
                    getAllProductsList()
                }
                if (response != null){
                    Session.totalProductList = response.body()?.data!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgress()
            }
            dismissProgress()
            initiateViews()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sub_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as Home).manageBottomBar(true)

        arguments?.let {
            if (it.containsKey("cat_id")){
                initiateViews()
            }else{
                getCategories()
            }
        }?: run {
            getCategories()
        }

    }

    override fun onRowClick(data: SubCategoryDetails) {
if(data!=null)
{
    getProducts(data.cat_id)
}
    }

    override fun onRowClick(data: ProductDetails) {
       if(data!=null)
       {
           val bundle = Bundle()
           bundle.putString("productid", data.pdt_id)

           /*(activity as Home).setFragment(ProductDetailsFragment(),
               FragmentConstants.PRODUCT_DETAILS_FRAGMENT, bundle, false)*/
           (activity as Home).onProductdetailsNav(bundle,data.pdt_name)
       }
    }


}
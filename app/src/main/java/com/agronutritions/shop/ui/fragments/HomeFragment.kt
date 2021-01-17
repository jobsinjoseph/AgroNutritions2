package com.agronutritions.shop.ui.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.BuildConfig
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.CategoryAdapter
import com.agronutritions.shop.adapters.HeaderPagerAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.roadmate.app.api.response.AppVersionMaster
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
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


class HomeFragment : BaseFragment(), CategoryAdapter.Categorycommunicator {

    private lateinit var headerPagerAdapter: HeaderPagerAdapter
    var headerPagerList: ArrayList<PagerDetails> = ArrayList()
    var categoriesList: ArrayList<CategoryDetails> = ArrayList()
    private lateinit var timer : Timer


    private fun initiateViews(){

        setupHeaderBanner()

        setupCategoryList()

    }

    private fun setupHeaderBanner(){
        if (Session.headerBannerItemsList.isEmpty()) {
            getBanners()
        } else {
            headerPagerList = Session.headerBannerItemsList
            showHeaderBanner()
            setupCategoryList()
        }
    }

    private fun setupCategoryList(){
        if (Session.mainCategoriesList.isEmpty()) {
            getCategories()
        } else {
            categoriesList = Session.mainCategoriesList
            showCategoriesList()
            if (Session.totalProductList.isEmpty()) {
                getAllProducts()
            }
        }


        if (Session.orderPrdtList.isNotEmpty()){
            (activity as Home).updateCountFlag(Session.orderPrdtList.size.toString())
        }
        else
        {
            (activity as Home).updateCountFlag(Session.orderPrdtList.size.toString())
        }
    }

    private fun showHeaderBanner(){
        headerPager.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        headerPagerAdapter = HeaderPagerAdapter(activity!!, headerPagerList)
        headerPager.adapter = headerPagerAdapter
        setupHeaderAutoScroll()

           /* if (Session.mainCategoriesList.isEmpty()) {
                getCategories()
            } else {
                categoriesList = Session.mainCategoriesList
                showCategoriesList()
            }*/

    }

    private fun showCategoriesList(){
        categoryList.setOrientation(DSVOrientation.HORIZONTAL)
        var  infiniteAdapter = InfiniteScrollAdapter.wrap( CategoryAdapter(activity!!, categoriesList,this));
        categoryList.adapter = infiniteAdapter
        //categoryList.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime())
        categoryList.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )

    }

    private fun checkAppVersion(){
        lifecycleScope.launch {
            val response = APIManager.call<StoreApiService, Response<AppVersionMaster>> {
                getAppVersionFromServer()
            }
            if (response.isSuccessful && !response.body()?.data!!.isNullOrEmpty()){
                val serverAppVersion = response.body()?.data!![0];
                if (serverAppVersion.version_code.toInt() > BuildConfig.VERSION_CODE && serverAppVersion.version_name != getAppVersionName()){
                    promptUpdate(serverAppVersion.version_name, getAppVersionName())
                }
            }
        }
    }

    private fun appVersionRequestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("apptype", "1")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun getAppVersionName(): String{
        var versionName = "0";
        try {
            val pInfo: PackageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            versionName = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return  versionName;
    }

    private fun promptUpdate(newVersion: String, oldversion: String) {
        AlertDialog.Builder(activity)
            .setTitle("Update available!")
            .setMessage("You are using an out dated version(v$oldversion) of RoadMate! An updated version(v$newVersion)available in Google Play Store.")
            .setPositiveButton("Update") { _, _ ->
                val appPackageName = requireActivity().packageName

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            .setNegativeButton(
                "Dismiss"
            ) { _, _ -> }
            .show()
    }

    private fun getBanners(){
        lifecycleScope.launch {
            //showProgress(activity, "Fetching data..")
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
                dismissProgress()
            }
            dismissProgress()
            showHeaderBanner()
            setupCategoryList()
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
                dismissProgress()
            }
            dismissProgress()
            showCategoriesList()
            if (Session.totalProductList.isEmpty()) {
                getAllProducts()
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

//            checkAppVersion()
        }
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

    override fun onRowClick(data: CategoryDetails) {
        if(data!=null)
        {
            val bundle = Bundle()
            bundle.putString("cat_id", data.cat_id)

            /*(activity as Home).setFragment(HomeSubCatFragment(),
                FragmentConstants.HOME_SUB_FRAGMENT, bundle, false)*/
            (activity as Home).onSubCategoryNav(bundle,data.cat_name)
        }
    }
}
package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.WalletsAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.WalletDataModel
import com.agronutritions.shop.api.response.WalletDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_walletshis.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class WalletsFragment : BaseFragment(){

    private lateinit var walletsAdapter: WalletsAdapter

    var walletList: ArrayList<WalletDetails> = ArrayList()
    private fun initViews(){
        walletsListRv.layoutManager = LinearLayoutManager(activity)
        getGenoeMenu()
    }

    private fun getGenoeMenu(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<WalletDataModel>> {
                    getWalletMaster(walletJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    walletList = response.body()?.wallet_list!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (walletList.isNotEmpty()){
                showHistory.visibility = View.VISIBLE
                emptyHistory.visibility = View.GONE
                showReportMenu()
            }else{
                showHistory.visibility = View.GONE
                emptyHistory.visibility = View.VISIBLE
            }
        }
    }
    private fun showReportMenu(){
        walletsAdapter = WalletsAdapter(activity!!, walletList){ _, menu ->
        }
        walletsListRv.adapter = walletsAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_walletshis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    private fun walletJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customer_id", UserInfo().uid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
}
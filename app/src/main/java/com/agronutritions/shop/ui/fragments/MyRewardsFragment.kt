package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.RewardsViewAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.RewardsDataModel
import com.agronutritions.shop.api.response.RewardsDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_rewardsview.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class MyRewardsFragment : BaseFragment(){

    private lateinit var rewardsAdapter: RewardsViewAdapter

    var rewardsList: ArrayList<RewardsDetails> = ArrayList()
    private fun initViews(){
        rewardsListRv.layoutManager = LinearLayoutManager(activity)
        getMyrewardsMenu()
    }

    private fun getMyrewardsMenu(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<RewardsDataModel>> {
                    getMyRewardsMaster(myRewardsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    rewardsList = response.body()?.rewards_list!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (rewardsList.isNotEmpty()){
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
        rewardsAdapter = RewardsViewAdapter(activity!!, rewardsList){ _, menu ->
        }
        rewardsListRv.adapter = rewardsAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rewardsview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    private fun myRewardsJsonRequest() : RequestBody {

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
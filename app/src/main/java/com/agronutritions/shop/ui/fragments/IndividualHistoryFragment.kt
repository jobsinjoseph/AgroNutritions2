package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.OrderIndividualHistoryAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.OrderTransHistoryData
import com.agronutritions.shop.api.response.OrderTransHistoryDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.ui.activities.BaseActivity
import kotlinx.android.synthetic.main.individual_history_fragment.*

import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class IndividualHistoryFragment : BaseFragment(){

    private lateinit var orderHistoryAdapter: OrderIndividualHistoryAdapter

    var orderHistoryList: ArrayList<OrderTransHistoryDetails> = ArrayList()

    var ord_mas_id = "0"

    private fun initViews(){
        ord_mas_id = arguments!!.getString("ord_mas_id")!!;
        historyListRv.layoutManager = LinearLayoutManager(activity)

        getIndividualHistory(ord_mas_id)
    }

    private fun getIndividualHistory(ord_mas_id: String){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching History..")
            try {
                val response =  APIManager.call<StoreApiService, Response<OrderTransHistoryData>> {
                    getOrderHistoryTrans(historyDetailsJsonRequest(ord_mas_id))
                }
                if (response != null && response.body()!!.message == "Success"){
                    orderHistoryList = response.body()?.data!!.ordr_single_det
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (orderHistoryList.isNotEmpty()){
                showHistory()
            }
        }
    }

    private fun historyDetailsJsonRequest(ord_mas_id: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("secure_key", activity!!.resources.getString(R.string.api_key))
            json.put("ord_mas_id",ord_mas_id)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showHistory(){
        orderHistoryAdapter = OrderIndividualHistoryAdapter(activity!!, orderHistoryList)
        historyListRv.adapter = orderHistoryAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.individual_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }
}

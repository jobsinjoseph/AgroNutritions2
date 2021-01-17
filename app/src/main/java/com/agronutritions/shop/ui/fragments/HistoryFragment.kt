package com.agronutritions.shop.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.OrderHistoryAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.OrderHistoryData
import com.agronutritions.shop.api.response.OrderHistoryDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class HistoryFragment : BaseFragment(), OrderHistoryAdapter.Invoicecommunicator {

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    var orderHistoryList: ArrayList<OrderHistoryDetails> = ArrayList()

    private fun initViews(){
        getHistory()
    }

    private fun getHistory(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching History..")
            try {
                val response =  APIManager.call<StoreApiService, Response<OrderHistoryData>> {
                    getOrderHistoryMaster(historyJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    orderHistoryList = response.body()?.ordr_history_det!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (orderHistoryList.isNotEmpty()){
                showHistory.visibility = View.VISIBLE
                emptyHistory.visibility = View.GONE
                showHistory1()
            }else{
                showHistory.visibility = View.GONE
                emptyHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun historyJsonRequest() : RequestBody {

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



    private fun showHistory1(){
        historyListRv.layoutManager = LinearLayoutManager(activity)
        orderHistoryAdapter = OrderHistoryAdapter(activity!!, orderHistoryList,this)
        historyListRv.adapter = orderHistoryAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    override fun onRowClick(data: OrderHistoryDetails) {
       if(data!=null)
       {
           val bundle = Bundle()
           bundle.putString("order_id", data.order_id)
           (activity as Home).onMyInvoiceViewNav("Invoice",bundle)
       }
    }
}
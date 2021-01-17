package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.SponserProfileDataModel
import com.agronutritions.shop.api.response.SponserprofdetailsData
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.sponser_data_view_fragment.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SponserProfileRepoFragment : BaseFragment(){
    lateinit var sponser_Data: SponserprofdetailsData
    private fun initViews(){
        getBankDets()
    }

    private fun getBankDets(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<SponserProfileDataModel>> {
                    getSponserMaster(sponserDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    sponser_Data = response.body()?.sponser_list!!
                    name.text=sponser_Data.name
                    address.text=sponser_Data.address
                    phone_number.text=sponser_Data.phone_number
                    ref_id.text=sponser_Data.ref_id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sponser_data_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    private fun sponserDetailsJsonRequest() : RequestBody {

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
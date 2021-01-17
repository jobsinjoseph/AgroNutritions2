package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.BankdetailsData
import com.agronutritions.shop.api.response.BankdetailsDataModel
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.bank_details_data_view_fragment.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class BankDetailsRepoFragment : BaseFragment(){
    lateinit var bankdets: BankdetailsData
    private fun initViews(){
        getBankDets()
    }

    private fun getBankDets(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<BankdetailsDataModel>> {
                    getBankMaster(bankDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    bankdets = response.body()?.bank_list!!
                    bank_holder.text=bankdets.bank_holder
                    bank_account_number.text=bankdets.bank_account_number
                    bank_name.text=bankdets.bank_name
                    bank_branch.text=bankdets.bank_branch
                    bank_ifsc.text=bankdets.bank_ifsc
                    customer_id.text=bankdets.customer_id

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bank_details_data_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    private fun bankDetailsJsonRequest() : RequestBody {

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
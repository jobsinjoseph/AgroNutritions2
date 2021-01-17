package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.GeneologysMenuAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.GeneologyDataModel
import com.agronutritions.shop.api.response.GenologyDetails
import com.agronutritions.shop.api.response.GetRefIdDataModel
import com.agronutritions.shop.api.response.ReferralDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_geneology_menu.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class GeneologysFragment : BaseFragment(){

    private lateinit var geneologysMenuAdapter: GeneologysMenuAdapter
    lateinit var refno:String
    var geneomenuList: ArrayList<GenologyDetails> = ArrayList()
    var geneoRefList: ArrayList<ReferralDetails> = ArrayList()
    private fun initViews(){
        geneoListRv.layoutManager = LinearLayoutManager(activity)
        getGenoeRefID()
    }
    private fun getGenoeRefID(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<GetRefIdDataModel>> {
                    getReferralID(geneologyRefJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    geneoRefList = response.body()?.refId_list!!
                    refno=geneoRefList[0].ref_id
                    UserInfo().rid =refno
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
            getGenoeMenu()
        }
    }


    private fun getGenoeMenu(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<GeneologyDataModel>> {
                    getgeneologyMaster(geneologyJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    geneomenuList = response.body()?.geno_list!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (geneomenuList.isNotEmpty()){
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
        geneologysMenuAdapter = GeneologysMenuAdapter(activity!!, geneomenuList){ _, menu ->
        }
        geneoListRv.adapter = geneologysMenuAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_geneology_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }

    private fun geneologyJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("referalnumber", UserInfo().rid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun geneologyRefJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customerid", UserInfo().uid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
}
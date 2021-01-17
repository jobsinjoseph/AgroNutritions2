package com.agronutritions.shop.ui.fragments
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.ReportsMenuAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.ReportMenuDetails
import com.agronutritions.shop.api.response.ReportMenusDataModel
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import kotlinx.android.synthetic.main.fragment_reports_menu.*
import kotlinx.coroutines.launch
import retrofit2.Response

class ReportsFragment : BaseFragment(){

    private lateinit var reportsAdapter: ReportsMenuAdapter

    var reportmenuList: ArrayList<ReportMenuDetails> = ArrayList()
    private fun initViews(){
        reportsListRv.layoutManager = GridLayoutManager(activity, 3)
        getReportMenu()
    }

    private fun getReportMenu(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Menus..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ReportMenusDataModel>> {
                    getReportMenusMaster()
                }
                if (response != null && response.body()!!.message == "Success"){
                    reportmenuList = response.body()?.menu_list!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()

            if (reportmenuList.isNotEmpty()){
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
        reportsAdapter = ReportsMenuAdapter(activity!!, reportmenuList){ _, menu ->

            if(TextUtils.equals(menu.menu_id,"1"))
            {
                (activity as Home).onGeneologyNav(menu.menu_name)
            }
            else if(TextUtils.equals(menu.menu_id,"2"))
            {
                (activity as Home).onMyOrderNav(menu.menu_name)
            }
            else if(TextUtils.equals(menu.menu_id,"3"))
            {
                (activity as Home).onWalletNav(menu.menu_name)
            }
           else if(TextUtils.equals(menu.menu_id,"4"))
            {
                (activity as Home).onBankDetNav(menu.menu_name)
            }
           else if(TextUtils.equals(menu.menu_id,"5"))
            {
                (activity as Home).onSponserprofNav(menu.menu_name)
            }
            else if(TextUtils.equals(menu.menu_id,"6"))
            {
                (activity as Home).onWalletAmtViewNav(menu.menu_name)
            }
            else if(TextUtils.equals(menu.menu_id,"7"))
            {
                (activity as Home).onRewardsViewNav(menu.menu_name)
            }
            else if(TextUtils.equals(menu.menu_id,"8"))
            {
                (activity as Home).onMyRewardsViewNav(menu.menu_name)
            }


        }
        reportsListRv.adapter = reportsAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reports_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        initViews()
    }
}
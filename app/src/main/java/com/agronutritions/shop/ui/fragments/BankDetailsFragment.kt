package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.BankDetailsResponseData
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_bank_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class BankDetailsFragment: BaseFragment(), DialogInteractionListener {

    var holderName = ""
    var accountNumber = ""
    var ifscCode = ""
    var bankName = ""
    var branchName = ""
    var payType = "1"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bank_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnk_pay_type.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group?.findViewById(group.checkedRadioButtonId) as? RadioButton
            checkedRadioButton?.let {

                if (checkedRadioButton.isChecked){
                    payType = checkedRadioButton.tag.toString()
                }

                if (checkedId == R.id.weekly){
                    payType = "1"
                }else{
                    payType = "2"
                }

            }
        }
        btn_next.setOnClickListener {
            if(isAllInputValid()){
                processBankDetails()
            }
        }
    }

    private fun processBankDetails(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Processing..")
            val response =  APIManager.call<StoreApiService, Response<BankDetailsResponseData>> {
                bankDetails(bankDetailsJsonRequest())
            }
            try {
                if (response != null&& TextUtils.equals(response.body()!!.error,"false")){

                    BaseActivity.dismissProgress()
                    Utils.showAlertSuccess(activity!!, resources.getString(R.string.app_name), "Bank details completed successfully!",
                        this@BankDetailsFragment, 26)
                }else{
                    BaseActivity.dismissProgress()
                    Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message,
                        this@BankDetailsFragment, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                BaseActivity.dismissProgress()
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message,
                    this@BankDetailsFragment, 0)
            }
        }
    }

    private fun bankDetailsJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("customerid", UserInfo().uid)
            json.put("accountnumber", accountNumber)
            json.put("holdername", holderName)
            json.put("bankbranch", branchName)
            json.put("bankname", bankName)
            json.put("bankifsc", ifscCode)
            json.put("incometype", payType)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun isAllInputValid(): Boolean{
        if (holdername.text.toString().isNotEmpty() &&
            bnk_account_no.text.toString().isNotEmpty() &&
            bank_ifsc_code.text.toString().isNotEmpty()

            &&
            bnk_name.text.toString().isNotEmpty()
            &&
            bnk_branch.text.toString().isNotEmpty()
            &&
            payType.isNotEmpty()
        ) {

            holderName = holdername.text.toString()
            branchName = bnk_branch.text.toString()
            bankName = bnk_name.text.toString()
            accountNumber =  bnk_account_no.text.toString()
            ifscCode = bank_ifsc_code.text.toString()
            return true
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(true, "Bank Details","",
            isMain = false,
            showBack = true
        )
    }

    override fun onPositiveResponse(id: Int) {
        if (id ==26){
            UserInfo().isBankInfoCompleted = true
            (requireActivity() as AuthenticationActivity).openTermsNConditionFragment()
        }
    }

    override fun onNegativeResponse(id: Int) {

    }
}
package com.agronutritions.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.LoginResponseDetails
import com.agronutritions.shop.api.response.OTPVerifyData
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.Constants
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class OTPFragment: BaseFragment(), DialogInteractionListener {

    lateinit var reg: LoginResponseDetails
    var regMobile = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.getParcelable<LoginResponseDetails>(Constants.SIGN_UP_DETAILS) != null){
            reg = arguments?.getParcelable<LoginResponseDetails>(Constants.SIGN_UP_DETAILS)!!
        } else if(arguments?.getString(Constants.MOBILE_NUMBER) != null){
            regMobile = arguments?.getString(Constants.MOBILE_NUMBER)!!
        } else {
            Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
        }

        btnSubmitOTP.setOnClickListener {
            if(ovVerifyOtpView.text?.isNotEmpty() == true && ovVerifyOtpView.text?.length == 4){
                if(this@OTPFragment::reg.isInitialized){
                    processOtp()
                }
                else
                {
                    processOtpFromLogin();
                }


            } else {
                Toast.makeText(requireContext(), "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserInfo(userData: LoginResponseDetails){
        UserInfo().uid = userData.cstmr_id
        UserInfo().name = userData.cstmr_name
        UserInfo().mob = userData.cstmr_mob
        UserInfo().email = userData.cstmr_gmail
    }

    private fun saveUserInfoSign(){
        UserInfo().uid = reg.cstmr_id
        UserInfo().name = reg.cstmr_name
        UserInfo().mob = reg.cstmr_mob
        UserInfo().email = reg.cstmr_gmail
        UserInfo().refBy = reg.cstmr_ref_by
    }

    private fun processOtp(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Processing..")
            val response =  APIManager.call<StoreApiService, Response<OTPVerifyData>> {
                otpChecking(otpJsonRequest(this@OTPFragment::reg.isInitialized))
            }
            try {
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){

//                    val userData = response.body()!!.data
                    if(this@OTPFragment::reg.isInitialized){
                        saveUserInfoSign()
                    } else {
                        UserInfo().mob = regMobile
//                        saveUserInfo()
                    }
                    UserInfo().uid = response.body()!!.data.toString()
                    BaseActivity.dismissProgress()

                    Utils.showAlertSuccess(activity!!, resources.getString(R.string.app_name), "Welcome! Successfully joined", this@OTPFragment, 24)
                }else{
                    BaseActivity.dismissProgress()
                    Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message, this@OTPFragment, 0)
                }
            } catch (e: Exception) {
                BaseActivity.dismissProgress()
                e.printStackTrace()
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message, this@OTPFragment, 0)
            }
        }
    }

    private fun processOtpFromLogin(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Processing..")
            val response =  APIManager.call<StoreApiService, Response<OTPVerifyData>> {
                otpCheckingLogin(otpJsonRequestL())
            }
            try {
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){

//                    val userData = response.body()!!.data
                    if(this@OTPFragment::reg.isInitialized){
                        saveUserInfoSign()
                    } else {
                        UserInfo().mob = regMobile
//                        saveUserInfo()
                    }
                    UserInfo().uid = response.body()!!.data.toString()
                    UserInfo().isProfileCompleted = true
                    UserInfo().isBankInfoCompleted = true
                    UserInfo().isTermsAccepted = true
                    BaseActivity.dismissProgress()
                    //(requireActivity() as AuthenticationActivity).openCompleteProfileFragment()
                    Utils.showAlertSuccess(activity!!, resources.getString(R.string.app_name), response.body()!!.message, this@OTPFragment, 29)
                }else{
                    BaseActivity.dismissProgress()
                    Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message, this@OTPFragment, 0)
                }
            } catch (e: Exception) {
                BaseActivity.dismissProgress()
                e.printStackTrace()
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), response.body()!!.message, this@OTPFragment, 0)
            }
        }
    }

    private fun otpJsonRequest(isFromSignUp: Boolean) : RequestBody {


        var jsonData = ""
        var json: JSONObject? = null
        if(isFromSignUp){
            try {
                json = JSONObject()
                json.put("ref_by", reg.cstmr_ref_by)
                json.put("otp", ovVerifyOtpView.text.toString())
                json.put("phone_number", reg.cstmr_mob)
                jsonData = json.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            try {
                json = JSONObject()
                json.put("ref_by", UserInfo().refBy)
                json.put("otp", ovVerifyOtpView.text.toString())
                json.put("phone_number", regMobile)
                jsonData = json.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return jsonData.toRequestBody()
    }

    private fun otpJsonRequestL() : RequestBody {


        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("otp", ovVerifyOtpView.text.toString())
            json.put("phonenumber", regMobile)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(true, "Verify OTP","",
            isMain = false,
            showBack = true
        )
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 24){
            (requireActivity() as AuthenticationActivity).openCompleteProfileFragment()
        }else if (id == 29){
            startActivity(Intent(activity, Home::class.java).putExtra("fromlogin", true))
            requireActivity().finish()
        }
    }

    override fun onNegativeResponse(id: Int) {

    }

}
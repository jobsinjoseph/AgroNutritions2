package com.agronutritions.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.LoginResponseData
import com.agronutritions.shop.api.response.LoginResponseDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.constants.BroadcastConstants
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.Constants
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class LoginFragment: BaseFragment(), DialogInteractionListener {

    var loginUserName = ""
    var loginPaassword = ""

    private fun resumeOrderUpload(){
        val intent = Intent(BroadcastConstants.ACTION_USER_JOINED)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    private fun processLogin(){
        lifecycleScope.launch {
            showProgress(activity, "Processing..")
            val response =  APIManager.call<StoreApiService, Response<LoginResponseData>> {
                loginUserOTP(loginJsonRequest())
            }
            try {
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){
                    dismissProgress()
                    val bundle = Bundle()
                    bundle.putString(Constants.MOBILE_NUMBER, loginUserName)
                    (requireActivity() as AuthenticationActivity).openOTPFragment(bundle)
                }else{
                    dismissProgress()
                    Utils.showAlertError(activity!!, "", response.body()!!.message, this@LoginFragment, 0)
                }
            } catch (e: Exception) {
                dismissProgress()
                e.printStackTrace()
                Utils.showAlertError(requireActivity(), "", response.body()!!.message, this@LoginFragment, 0)
            }
        }
    }

    private fun isAllInputValid(): Boolean{
        if (username.text.toString().isNotEmpty()){
            loginUserName = username.text.toString()
            return true
        }
        return false
    }

    private fun loginJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("phonenumber", loginUserName)
//            json.put("password", loginPaassword)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun saveUserInfo(userData: LoginResponseDetails){
        UserInfo().uid = userData.cstmr_id
        UserInfo().name = userData.cstmr_name
        UserInfo().mob = userData.cstmr_mob
        UserInfo().refBy = userData.cstmr_ref_by
        //UserInfo().email = userData.cstmr_gmail
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (activity as Home).manageBottomBar(false)

        btnNewJoin.setOnClickListener(View.OnClickListener {
//            (activity as Home).openSignUpFragment()
            (activity as AuthenticationActivity).openSignUpFragment()
        })

        btnLogin.setOnClickListener(View.OnClickListener {
            if (isAllInputValid()){
                processLogin()
            }else{
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), "Please enter your joined mobile number.", this, 0)
            }
        })

        promptSignup.setOnClickListener(View.OnClickListener {
//            (activity as Home).openSignUpFragment()
            (activity as AuthenticationActivity).openSignUpFragment()
        })

        //////////
//        val compoundDrawables: Array<Drawable> = pfle.compoundDrawables
//        val drawableLeft = compoundDrawables[0].mutate()
//        drawableLeft.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
//        val compoundDrawables1: Array<Drawable> = pwd.compoundDrawables
//        val drawableLeft1 = compoundDrawables1[0].mutate()
//        drawableLeft1.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(false, "Login","",
            isMain = false,
            showBack = false
        )
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 1){
            if (Session.orderPaymentInitiated){
                resumeOrderUpload()
            }else{
               (activity as  Home).onProfileTabSelectedBottomNav()
            }
        }
    }

    override fun onNegativeResponse(id: Int) {
    }
}
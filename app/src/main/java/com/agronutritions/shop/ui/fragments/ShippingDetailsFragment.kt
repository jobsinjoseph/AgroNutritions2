package com.agronutritions.shop.ui.fragments
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.SignupResponseData
import com.agronutritions.shop.api.response.SignupResponseDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.constants.BroadcastConstants
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ShippingDetailsFragment : BaseFragment(), DialogInteractionListener {

    var validationWarning = "All fields are mandatory"
    var regName = ""
    var regEmail = ""
    var regPhone = ""
    var regPassword = ""
    var regConfirmPassword = ""
    var regRefferalCode = ""
    var regAddress = ""
    private fun resumeOrderUpload(){
        (activity as  Home).onCartTabSelectedBottomNav()
        val intent = Intent(BroadcastConstants.ACTION_USER_JOINED)
        LocalBroadcastManager.getInstance(activity!!).sendBroadcast(intent)
    }

    private fun processSignup(){
        lifecycleScope.launch {
            showProgress(activity, "Processing..")
            val response =  APIManager.call<StoreApiService, Response<SignupResponseData>> {
                signupUser(signupJsonRequest())
            }
            try {
                if (response != null){

                    val userData = response.body()!!.data
                   var msg =response.body()!!.message
                    if(userData==0)
                    {
                        Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), msg, this@ShippingDetailsFragment, 0)
                    }
                    else if(userData==1)
                    {
                        Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), msg, this@ShippingDetailsFragment, 0)
                    }
                    else
                    {
                        Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), msg, this@ShippingDetailsFragment, 0)
                    }
                    //saveUserInfo(userData)

                    dismissProgress()

                    if (Session.orderPaymentInitiated){
                        resumeOrderUpload()
                    }else{
                        (activity as  Home).onProfileTabSelectedBottomNav()
                    }
                }else{
                    dismissProgress()
                    var error = "Registration Failed! Try again later."
                    if (response != null){
                        error = response.body()!!.error
                    }
                    Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), error, this@ShippingDetailsFragment, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgress()
                Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), "Registration Failed! Try again later.", this@ShippingDetailsFragment, 0)
            }
        }
    }

    /*private fun isAllInputValid(): Boolean{
        if (name.text.toString().isNotEmpty() &&
            email.text.toString().isNotEmpty() &&
            address.text.toString().isNotEmpty()&&
            phone.text.toString().isNotEmpty() &&
            passwordEt.text.toString().isNotEmpty() &&
            passwordConfirmEt.text.toString().isNotEmpty()){

            regName = name.text.toString()
            regEmail = email.text.toString()
            regPhone = phone.text.toString()
            regPassword = passwordEt.text.toString()
            regConfirmPassword = passwordConfirmEt.text.toString()
            regAddress=address.text.toString()
            regRefferalCode=refcode.text.toString()

            if (regPassword != regConfirmPassword){
                validationWarning = "Password miss match"
                passwordEt.error = ""
                passwordConfirmEt.error = ""
                return false
            }
            return true
        }
        return false
    }*/

    private fun signupJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("name", regName)
            json.put("phone_number", regPhone)
            json.put("address", regAddress)
            json.put("email", regEmail)
            json.put("password", regPassword)
            json.put("ref_by", regRefferalCode)//optional
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun saveUserInfo(userData: SignupResponseDetails){
        UserInfo().uid = userData.cstmr_id
        UserInfo().name = userData.cstmr_name
        UserInfo().mob = userData.cstmr_mob
        UserInfo().email = userData.cstmr_gmail
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shipping_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
        (activity as Home).manageBottomBar(false)

       /* signup.setOnClickListener(View.OnClickListener {
            if (isAllInputValid()){
                processSignup()
            }else{
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), validationWarning, this, 0)
            }
        })*/
    }

    override fun onPositiveResponse(id: Int) {
    }

    override fun onNegativeResponse(id: Int) {
    }
}
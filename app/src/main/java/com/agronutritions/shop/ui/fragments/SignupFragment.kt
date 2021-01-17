package com.agronutritions.shop.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.constants.BroadcastConstants
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.Constants
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SignupFragment : BaseFragment(), DialogInteractionListener {

    var validationWarning = "All fields are mandatory"
    var regName = ""
    var regEmail = ""
    var regPhone = ""
    var regPassword = ""
    var regConfirmPassword = ""
    var regRefferalCode = ""
    var regAddress = ""
    var retMsg:String?=""
    lateinit var refs_Data: RefraldetailsData

    var showVerification = false

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
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){
                    dismissProgress()
                    val loginResponseDetails = LoginResponseDetails(cstmr_id = response.body()!!.data.toString(),cstmr_mob = regPhone
                        , cstmr_gmail = regEmail, cstmr_name = regName, cstmr_ref_by = regRefferalCode)

                    saveUserInfoSign(loginResponseDetails)
//                    val bundle = Bundle()
//                    bundle.putParcelable(Constants.SIGN_UP_DETAILS, loginResponseDetails)
//                    (requireActivity() as AuthenticationActivity).openOTPFragment(bundle)
                    (requireActivity() as AuthenticationActivity).openCompleteProfileFragment()
                }else{
                    dismissProgress()
                    retMsg =response.body()!!.message
                    Utils.showAlertError(activity!!, "", retMsg!!, this@SignupFragment, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                dismissProgress()
                Utils.showAlertError(activity!!, "", "Registration Failed! Try again later.", this@SignupFragment, 0)
            }
        }
    }

    private fun saveUserInfoSign(reg: LoginResponseDetails){
        UserInfo().uid = reg.cstmr_id
        UserInfo().name = reg.cstmr_name
        UserInfo().mob = reg.cstmr_mob
        UserInfo().email = reg.cstmr_gmail
        UserInfo().refBy = reg.cstmr_ref_by
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(true, "Join","",
            isMain = false,
            showBack = true
        )
    }

    private fun isAllInputValid(tag: String): Boolean{
        if (name.text.toString().isNotEmpty() &&
           // email.text.toString().isNotEmpty() &&
            phone.text.toString().isNotEmpty() && (!cbRef.isChecked || (cbRef.isChecked && refId.text.toString().isNotEmpty()))) {

            regName = name.text.toString()
            regEmail = email.text.toString()
            regPhone = phone.text.toString()
            return true
        }
        return false
    }

    private fun signupJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()

            json.put("name", regName)
            json.put("phone_number", regPhone)
            json.put("email", regEmail)
            if (cbRef.isChecked) {
                json.put("ref_by", regRefferalCode)
            } else {
                json.put("ref_by", "")
            }
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

    private fun initiateSignUpProcess(){
        if (isAllInputValid(join_new.tag.toString())){
            showVerification = false
            if (cbRef.isChecked) {
                getCheckRefIds()
            } else {
                processSignup()
            }
        }else{
            Utils.showAlertError(activity!!, "", validationWarning, this, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        signup.setOnClickListener(View.OnClickListener {
            initiateSignUpProcess()
        })

        login.setOnClickListener {
            (requireActivity() as AuthenticationActivity).openLoginFragment()
        }

        join_new.setOnClickListener {
            when(it.tag){
                "1" -> {
                    btn_chk_ref.visibility = View.GONE
                    refId.visibility = View.GONE
                    join_new.tag = "0"
                    signup.text = getString(R.string.join_new)
                    join_new.text = getString(R.string.join_now)
                }
                "0" -> {
                    btn_chk_ref.visibility = View.VISIBLE
                    refId.visibility = View.VISIBLE
                    join_new.tag = "1"
                    signup.text = getString(R.string.join_now)
                    join_new.text = getString(R.string.join_new)
                }
            }
        }

        //check referral code
        btn_chk_ref.setOnClickListener {

            showVerification = true
            if (refId.text.toString().isNotEmpty())
            {
                getCheckRefIds()
            }
            else
            {
                Utils.showAlertError(activity!!,"", validationWarning, this, 0)
            }
        }

        cbRef.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                ll_ref.visibility = View.VISIBLE
            }else{
                refId.setText("")
                ll_ref.visibility = View.GONE
            }
        }

    }

    override fun onPositiveResponse(id: Int) {
    }

    override fun onNegativeResponse(id: Int) {
    }

    private fun getCheckRefIds(){
        lifecycleScope.launch {
            showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<CheckReferralDataModel>> {
                    getCheckReferralUser(refDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success" && showVerification){
                    refs_Data = response.body()?.refer_list!!
                    regRefferalCode = refId.text.toString()
                    dismissProgress()
                    showDialog("Referral Info",refs_Data)
                }else if (response != null && response.body()!!.message == "Success" && !showVerification){
                    refs_Data = response.body()?.refer_list!!
                    regRefferalCode = refId.text.toString()
                    dismissProgress()
                    processSignup()
                }
                else{
                    dismissProgress()
                    Utils.showAlertError(requireContext(), "", "Invalid Reference Id!\nPlease try again.", this@SignupFragment, 0)
                }
            } catch (e: Exception) {
                dismissProgress()
                e.printStackTrace()
            }
        }
    }
    private fun refDetailsJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("referalnumber", refId.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
    private fun showDialog(title: String,refs_Data: RefraldetailsData) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_ref)
        val body = dialog.findViewById(R.id.body) as TextView
        body.text = title

        var name = dialog.findViewById(R.id.name) as TextView
        var address = dialog.findViewById(R.id.address) as TextView
        var phone_number = dialog.findViewById(R.id.phone_number) as TextView
        var ref_id = dialog.findViewById(R.id.ref_id) as TextView

        name.text="Name : " +refs_Data.name
        address.text="Address : " + refs_Data.address
        phone_number.text="Phone Number : " + refs_Data.phone_number
        ref_id.text="ReferralID : " + refs_Data.ref_id

        val yesBtn = dialog.findViewById(R.id.yesBtn) as TextView
        val noBtn = dialog.findViewById(R.id.noBtn) as TextView
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
}
package com.agronutritions.shop.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner

import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.DialogInteractionListener
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class ProfileFragment : BaseFragment(), DialogInteractionListener{
    lateinit var bankdets: BankdetailsData
    lateinit var profiledets: ProfileViewdetailsData
    lateinit var deliverydets: DeliveryAddressdetailsData
    var item1:String=""
    var statesList: ArrayList<StateDetails> = ArrayList()
    private fun removeUser(){
        UserInfo().uid = ""
        UserInfo().name = ""
        UserInfo().email = ""
        UserInfo().mob = ""

        profileLay.visibility = View.GONE
        loginLay.visibility = View.VISIBLE

        Toast.makeText(activity, "You have logged out!", Toast.LENGTH_SHORT).show()
    }

    private fun initializeViews(){
        getProfileDets()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as Home).manageBottomBar(true)
        btnLogin.setOnClickListener(View.OnClickListener {
            (activity as Home).openLoginFragment()
        })

        btnLogout.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, AuthenticationActivity::class.java))
            //Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), "Confirm Logout?", this, 1)
        })

        tv_edit.setOnClickListener {

            val dialogBuilder: AlertDialog = android.app.AlertDialog.Builder(activity).create()
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.custom_dialog, null)
            dialogBuilder.setCancelable(false)
            val edt_name =
                dialogView.findViewById<View>(R.id.edt_name) as EditText
            val edt_address =
                dialogView.findViewById<View>(R.id.edt_address) as EditText
            val edt_phone =
                dialogView.findViewById<View>(R.id.edt_phone) as EditText

            edt_name.setText(profiledets.name)
            edt_address.setText(profiledets.address)
            edt_phone.setText(profiledets.phone_number)

            val button1: Button =
                dialogView.findViewById<View>(R.id.buttonSubmit) as Button
            val button2: Button =
                dialogView.findViewById<View>(R.id.buttonCancel) as Button

            button2.setOnClickListener(View.OnClickListener { dialogBuilder.dismiss() })
            button1.setOnClickListener(View.OnClickListener {
                // DO SOMETHINGS
                getProfileEdits(edt_name.text.toString(),edt_address.text.toString(),edt_phone.text.toString())
                dialogBuilder.dismiss()


            })

            dialogBuilder.setView(dialogView)
            dialogBuilder.show()
        }

        tv_edit1.setOnClickListener {
            val dialogBuilder: AlertDialog = android.app.AlertDialog.Builder(activity).create()
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.custom_dialog_delivery, null)
            dialogBuilder.setCancelable(false)
            val del_username =
                dialogView.findViewById<View>(R.id.del_username) as EditText
            val del_address =
                dialogView.findViewById<View>(R.id.del_address) as EditText
            val del_phone =
                dialogView.findViewById<View>(R.id.del_phone) as EditText

            val del_country =
                dialogView.findViewById<View>(R.id.del_country) as EditText
            val del_state =
                dialogView.findViewById<View>(R.id.del_state) as AppCompatSpinner
            val del_city =
                dialogView.findViewById<View>(R.id.del_city) as EditText

            val del_pincode =
                dialogView.findViewById<View>(R.id.del_pincode) as EditText
            /////
            val del_housename =
                dialogView.findViewById<View>(R.id.del_housename) as EditText
            val del_town =
                dialogView.findViewById<View>(R.id.del_town) as EditText
            val del_street =
                dialogView.findViewById<View>(R.id.del_street) as EditText
            ////

            del_username.setText(deliverydets.name)
            del_address.setText(deliverydets.address)
            del_phone.setText(deliverydets.phone_number)
            del_country.setText(deliverydets.country)
            //del_state.setText(deliverydets.state)
            del_city.setText(deliverydets.city)
            del_pincode.setText(deliverydets.pincode)

            ///////
            del_housename.setText(deliverydets.house_name)
            del_town.setText(deliverydets.town)
            del_street.setText(deliverydets.street)


            val button1: Button =
                dialogView.findViewById<View>(R.id.buttonSubmit) as Button
            val button2: Button =
                dialogView.findViewById<View>(R.id.buttonCancel) as Button


            del_state?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if(position>0)
                    {
                        item1 = parent!!.getItemAtPosition(position).toString()
                        // Showing selected spinner item
                        // Toast.makeText(parent.context, "Selected: $item1", Toast.LENGTH_LONG).show()
                    }
                }

            }
            button2.setOnClickListener(View.OnClickListener { dialogBuilder.dismiss() })
            button1.setOnClickListener(View.OnClickListener {

                // DO SOMETHINGS
                getDeliveryEdits(del_username.text.toString(),del_address.text.toString(),del_phone.text.toString(),
                    del_country.text.toString(),item1,del_city.text.toString(),del_pincode.text.toString(),del_housename.text.toString(),del_town.text.toString(),del_street.text.toString())
                dialogBuilder.dismiss()
            })
            getStates(del_state)
            dialogBuilder.setView(dialogView)
            dialogBuilder.show()

        }

        tv_edit2.setOnClickListener {

            val dialogBuilder: AlertDialog = android.app.AlertDialog.Builder(activity).create()
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.custom_dialog_bank_details, null)
            dialogBuilder.setCancelable(false)

            val bank_holder =
                dialogView.findViewById<View>(R.id.bank_holder) as EditText
            val bank_account_number =
                dialogView.findViewById<View>(R.id.bank_account_number) as EditText
            val bank_name =
                dialogView.findViewById<View>(R.id.bank_name) as EditText

            val bank_branch =
                dialogView.findViewById<View>(R.id.bank_branch) as EditText
            val bank_ifsc =
                dialogView.findViewById<View>(R.id.bank_ifsc) as EditText


            bank_holder.setText(bankdets.bank_holder)
            bank_account_number.setText(bankdets.bank_account_number)
            bank_name.setText(bankdets.bank_name)
            bank_branch.setText(bankdets.bank_branch)
            bank_ifsc.setText(bankdets.bank_ifsc)

            val button1: Button =
                dialogView.findViewById<View>(R.id.buttonSubmit) as Button
            val button2: Button =
                dialogView.findViewById<View>(R.id.buttonCancel) as Button

            button2.setOnClickListener(View.OnClickListener { dialogBuilder.dismiss() })
            button1.setOnClickListener(View.OnClickListener { // DO SOMETHINGS
                getBankEdits(bank_holder.text.toString(),bank_account_number.text.toString(),bank_name.text.toString(),bank_branch.text.toString(),bank_ifsc.text.toString())
                dialogBuilder.dismiss()
            })

            dialogBuilder.setView(dialogView)
            dialogBuilder.show()

        }


    }

    override fun onResume() {
        super.onResume()
        if (UserInfo().uid.isNotEmpty()){
            profileLay.visibility = View.VISIBLE
            loginLay.visibility = View.GONE
            initializeViews()
        }else{
            profileLay.visibility = View.GONE
            loginLay.visibility = View.VISIBLE
        }
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 1){
            removeUser()
        }
    }

    override fun onNegativeResponse(id: Int) {

    }

    private fun getProfileDets(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProfileViewDataModel>> {
                    getProfileiew(profileDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    profiledets = response.body()?.profile_Data!!
                    username.text=profiledets.name
                    address.text= profiledets.address
                    phone.text= profiledets.phone_number
                    referralId.text=profiledets.ref_id

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
            getDeliveryDets()
        }
    }

    private fun getDeliveryDets(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<DeliveryAddressDataModel>> {
                    viewDeliveryAddress(profileDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    deliverydets = response.body()?.delivery_Data!!
                    del_username.text=deliverydets.name
                    del_address.text=deliverydets.address
                    del_phone.text=deliverydets.phone_number

                    del_country.text=deliverydets.country
                    del_state.text=deliverydets.state
                    del_city.text=deliverydets.city
                    del_pincode.text=deliverydets.pincode

                    del_housename.text=deliverydets.house_name
                    del_town.text=deliverydets.town
                    del_street.text=deliverydets.street


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
            getBankDets()
        }
    }

    private fun profileDetailsJsonRequest() : RequestBody {

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

    private fun getBankDets(){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<BankdetailsDataModel>> {
                    getBankMaster(profileDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    bankdets = response.body()?.bank_list!!
                    bank_holder.text=bankdets.bank_holder
                    bank_account_number.text="Account Number : " + bankdets.bank_account_number
                    bank_name.text="Bank Name    : " + bankdets.bank_name
                    bank_branch.text="Branch     : " + bankdets.bank_branch
                    bank_ifsc.text="IFSC Code    : " + bankdets.bank_ifsc
                    customer_id.text="CustomerID : " + bankdets.customer_id

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
        }
    }


    ////
    private fun getProfileEdits(
        name: String,
        address: String,
        phone: String
    ) {
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<ProfileEditDataModelResp>> {
                    editProfileiew(profileEditJsonRequest(name,address,phone))
                }
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){
                    //show message here
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


        }
    }


    //bankdetails
    private fun getBankEdits(
        bank_holder: String,
        accountno: String,
        bank_name: String,
        bank_branch: String,
        bank_ifsc: String
    ) {
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<BankEditDataModelResp>> {
                    editBankDetails(BankEditJsonRequest(bank_holder,accountno,bank_name,bank_branch,bank_ifsc))
                }
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){
                    //show message here
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


        }
    }



    private fun profileEditJsonRequest(
        name: String,
        address: String,
        phone: String
    ): RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("name", name)
            json.put("phone_number", phone)
            json.put("address", address)
            json.put("customer_id", UserInfo().uid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

//
    private fun profiledeliveryDetailsJsonRequest(
    delUsername: String,
    delAddress: String,
    delPhone: String,
    delCountry: String,
    delState: String,
    delCity: String,
    delPincode: String,del_house_name:String,del_town:String,del_street:String
): RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("name", delUsername)
            json.put("phone_number", delPhone)
            json.put("address", delAddress)
            json.put("country", delCountry)
            json.put("city", delCity)
            json.put("pincode", delPincode)
            json.put("state", delState)
            json.put("customer_id", UserInfo().uid)

            //////
            json.put("house_name", del_house_name)
            json.put("street", del_street)
            json.put("town", del_town)

            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    //
    private fun BankEditJsonRequest(
        holdername: String,
        accountnumber: String,
        bankname: String,
        bankbranch: String,
        bankifsc: String
    ): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customerid", UserInfo().uid)
            json.put("holdername", holdername)
            json.put("accountnumber", accountnumber)
            json.put("bankname", bankname)
            json.put("bankbranch", bankbranch)
            json.put("bankifsc", bankifsc)
            json.put("incometype", "3")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }


    private fun getDeliveryEdits(
        del_username: String,
        del_address: String,
        del_phone: String,
        del_country: String,
        del_state: String,
        del_city: String,
        del_pincode: String,del_house_name:String,del_town:String,del_street:String


    ) {
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<DeliveryEditDataModelResp>> {
                    editDeliveryDetails(profiledeliveryDetailsJsonRequest(del_username,del_address,del_phone,del_country,del_state,del_city,del_pincode,del_house_name,del_town,del_street))
                }
                if (response != null && TextUtils.equals(response.body()!!.error,"false")){
                    //show message here
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(activity,response.body()!!.message,Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


        }
    }
    private fun getStates(spn:AppCompatSpinner){
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<StateDataModel>> {
                    getstatesMaster()
                }
                if (response != null && response.body()!!.message == "Success"){
                    statesList = response.body()?.state_list!!
                    setStates(statesList,spn)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
        }
    }


    private fun setStates(
        statesList: ArrayList<StateDetails>,
        spn: AppCompatSpinner
    )
    {
        var list:ArrayList<String>?= ArrayList()
        list!!.add("Select State")
        for(i in 0 until statesList.size)
        {
            list!!.add(statesList[i].state)
        }
        val statesdataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this!!.activity!!, R.layout.spinner_item,
            list!!
        )
        statesdataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        // attaching data adapter to spinner
        spn.adapter = statesdataAdapter
    }

}
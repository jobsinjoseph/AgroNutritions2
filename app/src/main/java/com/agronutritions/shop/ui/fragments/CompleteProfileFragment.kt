package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.CompleteProfileResponseData
import com.agronutritions.shop.api.response.StateDataModel
import com.agronutritions.shop.api.response.StateDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_complete_details.*

import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class CompleteProfileFragment : BaseFragment(), DialogInteractionListener {
    var statesList: ArrayList<StateDetails> = ArrayList()
    var validationWarning = "All fields are mandatory"

    var fullName = ""
    var Address = ""
    var delivery_name1 = ""
    var phone_number1 = ""
    var country = ""
    var city1 = ""
    var pincode1 = ""
    var state1 = ""
    var item: String = ""
    var item1: String = ""
    var housename: String = ""
    var town1: String = ""
    var street1: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete_details, container, false)
    }

    private fun processCompleteProfile() {
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Processing..")
            val response = APIManager.call<StoreApiService, Response<CompleteProfileResponseData>> {
                completeProfile(completeProfileJsonRequest())
            }
            try {

                response.apply {
                    if (isSuccessful && TextUtils.equals(body()?.error, "false")) {
                        BaseActivity.dismissProgress()
                        Utils.showAlertSuccess(
                            activity!!,
                            resources.getString(R.string.app_name),
                            "Profile completed successfully!",
                            this@CompleteProfileFragment,
                            25
                        )
                    } else {
                        BaseActivity.dismissProgress()
                        Utils.showAlertError(
                            activity!!,
                            resources.getString(R.string.app_name),
                            response.body()!!.message,
                            this@CompleteProfileFragment,
                            0
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                BaseActivity.dismissProgress()
                Utils.showAlertError(
                    requireContext(),
                    resources.getString(R.string.app_name),
                    "OOPS! Something went wrong",
                    this@CompleteProfileFragment,
                    0
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(
            true, "Complete Profile", "",
            isMain = false,
            showBack = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_next_c.setOnClickListener {
            if (isAllInputValid()) {
                processCompleteProfile()
            }
        }

        val country: MutableList<String> = ArrayList()
        country.add("Select Country")
        country.add("India")

        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this!!.activity!!, R.layout.spinner_item, country)
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        // attaching data adapter to spinner
        cntry.adapter = dataAdapter

        cntry?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                item = parent!!.getItemAtPosition(position).toString()
                // Showing selected spinner item
                //Toast.makeText(parent.context, "Selected: $item", Toast.LENGTH_LONG).show()
            }

        }


        state?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    item1 = parent!!.getItemAtPosition(position).toString()
                    // Showing selected spinner item
                    // Toast.makeText(parent.context, "Selected: $item1", Toast.LENGTH_LONG).show()
                }
            }

        }

        getStates()
    }

    private fun completeProfileJsonRequest(): RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            if (checkBox3.isChecked) {

            }
            json.put("customer_id", UserInfo().uid)
            json.put("fullname", fullName)
            json.put("address", Address)
            json.put("shipping_name", delivery_name1)
            json.put("deladdress", Address)
            json.put("phone_number", UserInfo().mob)
            json.put("country", item)
            json.put("city", city1)
            json.put("pincode", pincode1)
            json.put("state", item1)

            //new fields
            json.put("house_name", housename)
            json.put("street", street1)
            json.put("town", town1)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun isAllInputValid(): Boolean {
        if (name.text.toString().isNotEmpty() &&
            edt_address.text.toString().isNotEmpty() &&
            delivery_name.text.toString().isNotEmpty()
            &&
            phone_number.text.toString().isNotEmpty()
            &&
            cntry.selectedItem.toString().trim() != "Select"
            &&
            city.text.toString().isNotEmpty()
            &&
            pincode.text.toString().isNotEmpty()
            &&
            state.selectedItem.toString().trim() != "Select"
        ) {

            fullName = name.text.toString()
            Address = edt_address.text.toString()
            delivery_name1 = delivery_name.text.toString()
            phone_number1 = phone_number.text.toString()
            //country = item
            city1 = city.text.toString()
            pincode1 = pincode.text.toString()
            //state1 = state.text.toString()
            housename = house_name.text.toString()
            town1 = town.text.toString()
            street1 = street.text.toString()
            return true
        }

        return false
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 25) {
            UserInfo().isProfileCompleted = true
            (requireActivity() as AuthenticationActivity).openBankDetailsFragment()
        }
    }

    override fun onNegativeResponse(id: Int) {
    }


    //states

    private fun getStates() {
        lifecycleScope.launch {
            BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response = APIManager.call<StoreApiService, Response<StateDataModel>> {
                    getstatesMaster()
                }
                if (response != null && response.body()!!.message == "Success") {
                    statesList = response.body()?.state_list!!
                    setStates(statesList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()
        }
    }

    private fun setStates(statesList: ArrayList<StateDetails>) {
        var list: ArrayList<String>? = ArrayList()
        list!!.add("Select State")
        for (i in 0 until statesList.size) {
            list!!.add(statesList[i].state)
        }
        val statesdataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this!!.activity!!, R.layout.spinner_item,
            list!!
        )
        statesdataAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        // attaching data adapter to spinner
        state.adapter = statesdataAdapter
    }
}
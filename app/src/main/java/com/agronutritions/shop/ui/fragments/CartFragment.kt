package com.agronutritions.shop.ui.fragments
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.CartListAdapter
import com.agronutritions.shop.adapters.PaymentModeAdapter
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.response.CommonResponse
import com.agronutritions.shop.api.response.DeliveryAddressDataModel
import com.agronutritions.shop.api.response.DeliveryAddressdetailsData
import com.agronutritions.shop.api.response.PaymentModeDetails
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.Session.Companion.orderPaymentInitiated
import com.agronutritions.shop.app.Session.Companion.orderPaymentModeId
import com.agronutritions.shop.app.Session.Companion.orderPrdtList
import com.agronutritions.shop.constants.BroadcastConstants
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.BaseActivity
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.dismissProgress
import com.agronutritions.shop.ui.activities.BaseActivity.Companion.showProgress
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.PincodeListener
import com.agronutritions.shop.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class CartFragment : BaseFragment(), DialogInteractionListener, PincodeListener {

    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var paymentModeAdapter: PaymentModeAdapter
    var deliveryPin = "0"
    var userAddress = ""
    var userRemarks = ""
    var behavior: BottomSheetBehavior<*>? = null
    var paymentModeList: ArrayList<PaymentModeDetails> = ArrayList()
    lateinit var deliverydets: DeliveryAddressdetailsData
    private fun initiateViews(){
        behavior = BottomSheetBehavior.from(bottom_sheet_cart)

        locationSub.text = com.agronutritions.shop.app.Session.userLocationSub
        deliveryPin = com.agronutritions.shop.app.Session.pincodeId


        paymentModeList.add(PaymentModeDetails("Select", "0"))
        paymentModeList.add(PaymentModeDetails("Cash On Delivery", "1"))
        paymentModeList.add(PaymentModeDetails("Online", "2"))

        cartItemsList.layoutManager = LinearLayoutManager(activity)
        cartListAdapter = CartListAdapter(activity!!, orderPrdtList){ _, orderProduct->
            updatePaymentTotal()
            if (orderProduct != null && orderProduct.pdt_qty.toDouble()>0) {
                (activity as Home).dbAddProductToCart(orderProduct!!)
            }else if (orderProduct != null && orderProduct.pdt_qty.toDouble()==0.0){
                (activity as Home).dbRemoveProductFromCart(orderProduct!!.pdt_id)
            }
        }
        cartItemsList.adapter = cartListAdapter

        updatePaymentTotal()

        if (orderPaymentModeId == "0") {
            payMode.text = "Select"
        }else if (orderPaymentModeId == "1") {
            payMode.text = "Cash On Delivery"
        }
        else {
            payMode.text = "Online"
        }

        //itemcount
        tv_cart_item_count.text= orderPrdtList.size.toString()+" "+"Items"

        tv_chk_out.setOnClickListener {
            /*(activity as Home).setFragment(ShippingDetailsFragment(),
                FragmentConstants.SHIPPING_FRAGMENT, null, false)*/
            //uploadOrder("")
            (activity as Home).processRazorPayPayment(getCartItemsTotal().toDouble())
//            uploadOrder("test1313")
//            Utils.showSuccessDialog(activity!!, "tid", getCartItemsTotal().toString(), response.body()!!.message, this@CartFragment, 3)
        }
        getDeliveryDets()
    }

    private fun updatePaymentTotal(){
        if (orderPrdtList.size>0) {
            itemTotal.text = activity!!.resources.getString(R.string.Rs) + String.format("%.2f", getCartItemsTotal())
            tax.text = activity!!.resources.getString(R.string.Rs) + "00"
            grandTotal.text = activity!!.resources.getString(R.string.Rs) + String.format("%.2f", getCartItemsTotal())
            dispPrice.text = activity!!.resources.getString(R.string.Rs) + " " + String.format("%.2f", getCartItemsTotal())
        } else {
            (activity as Home).onBackPressed()
        }
    }

    private fun getCartItemsTotal() : Double{
        var total = 0.0
        for (i in 0 until orderPrdtList.size){
            total += (orderPrdtList[i].pdt_total_price).toDouble()
        }
        return total
    }

    fun alertUpload(){
        orderPaymentInitiated = true
        if (UserInfo().uid.isNotEmpty() && UserInfo().uid != "0") {
            Utils.showAlertWarning(activity!!, resources.getString(R.string.app_name), "Confirm placing order?", this, 1)
        } else {
            (activity as Home).openLoginFragment()
        }
    }

    private fun isValidAddressAndPin(): Boolean{
        userAddress = address.text.toString()
        userRemarks = remarks.text.toString()
        if ((deliveryPin == "0" || deliveryPin.isEmpty()) && (userAddress.isEmpty())){
            Utils.showAlertError(activity!!, resources.getString(R.string.app_name),
                "Please select a delivery pin code & provide your shipping address.", this, 0)
            return  false
        }else if (deliveryPin == "0" || deliveryPin.isEmpty()){
            Utils.showAlertError(activity!!, resources.getString(R.string.app_name),
                "Please select a pin delivery code", this, 0)
            return  false
        }else if (userAddress.isEmpty()){
            Utils.showAlertError(activity!!, resources.getString(R.string.app_name),
                "Please provide your shipping address.", this, 0)
            return  false
        }else if (orderPaymentModeId == "0"){
            Utils.showAlertError(activity!!, resources.getString(R.string.app_name),
                "Please select your payment mode.", this, 0)
            return  false
        }
        else{
            return true
        }
    }

    fun uploadOrder(tid: String){
        dismissProgress()
        lifecycleScope.launch {
            showProgress(activity, "Processing..")
            try {
                val response =  APIManager.call<StoreApiService, Response<CommonResponse>> {
                    insertOrder(createJsonRequest(tid))
                }
                response.apply {
                    if (isSuccessful && TextUtils.equals(body()!!.error,"false")){
                        dismissProgress()
                        Utils.showSuccessDialog(activity!!, tid, getCartItemsTotal().toString(),
                            body()!!.data.toString(), this@CartFragment, 2)
                    }else{
                        dismissProgress()
                        var error = "OOPS! Failed to place order."
                        Utils.showAlertError(activity!!, resources.getString(R.string.app_name), error, this@CartFragment, 0)
                    }
                }
            } catch (e: Exception) {
                dismissProgress()
                e.printStackTrace()
                Utils.showAlertError(activity!!, resources.getString(R.string.app_name), "OOPS! Failed to place order.", this@CartFragment, 0)
            }
        }
    }

    fun paymentFailed(message: String){
        dismissProgress()
        Utils.showAlertError(activity!!, resources.getString(R.string.app_name), message, this@CartFragment, 0)
    }

    private fun createJsonRequest(tid: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customer_id", UserInfo().uid)
            json.put("name", UserInfo().name)
            json.put("phone_number", UserInfo().mob)

            json.put("address", UserInfo().addresss)
            json.put("country", UserInfo().country)
            json.put("city", UserInfo().city)
            json.put("pincode", UserInfo().pincodes)
            json.put("state", UserInfo().states)
            json.put("gtol", getCartItemsTotal().toString())
            if (tid.isNotEmpty()) {
                json.put("transaction_id", tid)
            } else {
                json.put("tid", "0")
            }

            val jsonArray: JSONArray = JSONArray()
            for (i in 0 until orderPrdtList.size){
                var jsonObject: JSONObject = JSONObject()
                jsonObject.put("productid", orderPrdtList[i].pdt_id)
                jsonObject.put("qty", orderPrdtList[i].pdt_qty)
                jsonObject.put("product_price", orderPrdtList[i].pdt_price)

                jsonArray.put(jsonObject)
            }
            json.put("order_transaction", jsonArray)

            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populatePaymentModeList(){
        payModeList.setHasFixedSize(true);
        payModeList.layoutManager = LinearLayoutManager(activity);

        paymentModeAdapter = PaymentModeAdapter(activity!!, paymentModeList){_, payModeName, payModeId ->
            payMode.text = payModeName
            orderPaymentModeId = payModeId
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
        }
        payModeList.adapter = paymentModeAdapter
        behavior!!.state = BottomSheetBehavior.STATE_EXPANDED;
    }

    private fun registerReceiver(){
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction(BroadcastConstants.ACTION_USER_JOINED)
//        LocalBroadcastManager.getInstance(context!!).registerReceiver(OnNotice(), mIntentFilter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    override fun onPause() {
        super.onPause()
//        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(OnNotice())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        registerReceiver()
        (activity as Home).manageBottomBar(false)
        initiateViews()

        /*tv_chk_out.setOnClickListener {
            if (isValidAddressAndPin()) {
                alertUpload()
            }
        }*/

        editLay.setOnClickListener(View.OnClickListener {
            (activity as Home).getPinCode(this)
        })

        payModeLay.setOnClickListener(View.OnClickListener {
            populatePaymentModeList()
        })
    }

    override fun onPositiveResponse(id:Int) {
        if (id == 3) {
            /*if (orderPaymentModeId == "1") {
                uploadOrder("")
            } else {*/
                showProgress(activity, "Processing..")
                (activity as Home).processRazorPayPayment(getCartItemsTotal().toDouble())
            //}
        } else if (id == 2) {
            (activity as Home).dbEmptyMyCart()
        }
    }

    override fun onNegativeResponse(id:Int) {
    }

    override fun onPincodeUpdated() {
        locationSub.text = com.agronutritions.shop.app.Session.userLocationSub
        deliveryPin = com.agronutritions.shop.app.Session.pincodeId
    }

    /*inner class OnNotice: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action.equals(BroadcastConstants.ACTION_USER_JOINED) && orderPaymentInitiated) {
                alertUpload()
            }
        }

    }*/


    private fun getDeliveryDets(){
        lifecycleScope.launch {
            //BaseActivity.showProgress(activity, "Fetching Data..")
            try {
                val response =  APIManager.call<StoreApiService, Response<DeliveryAddressDataModel>> {
                    viewDeliveryAddress(profileDetailsJsonRequest())
                }
                if (response != null && response.body()!!.message == "Success"){
                    deliverydets = response.body()?.delivery_Data!!
                    UserInfo().name=deliverydets.name
                    UserInfo().addresss= deliverydets.address
                    UserInfo().mob=deliverydets.phone_number
                    UserInfo().country=deliverydets.country
                    UserInfo().states= deliverydets.state
                    UserInfo().city=deliverydets.city
                    UserInfo().pincodes= deliverydets.pincode

                    //set to userinfo



                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            BaseActivity.dismissProgress()


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
}
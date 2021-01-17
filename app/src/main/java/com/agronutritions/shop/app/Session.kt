package com.agronutritions.shop.app

import com.agronutritions.shop.api.response.*
import com.agronutritions.shop.model.OrderProductData

class Session {
    companion object{
        /*var userLocationMain : String = "Agro Nutritions"
        var userLocationSub : String = "Select Pincode"*/
        var userLocationMain : String = ""
        var userLocationSub : String = ""
        var pincodeId : String = ""
        var orderPrdtList : ArrayList<OrderProductData> = ArrayList()
        var pinCodeItemsList:  ArrayList<PinDetails> = ArrayList()
        var headerBannerItemsList:  ArrayList<PagerDetails> = ArrayList()
        var mainCategoriesList:  ArrayList<CategoryDetails> = ArrayList()
        var mainsubCategoriesList:  ArrayList<SubCategoryDetails> = ArrayList()
        //var mainsProductList:  ArrayList<ProductDetails> = ArrayList()
        var mainProductList:  ArrayList<ProductDetails> = ArrayList()
        var totalProductList:  ArrayList<ProductDetails> = ArrayList()
        var orderPaymentInitiated : Boolean = false
        var orderPaymentModeId : String = "0"
        var lastPaymentTransactionId : String = "0"
        var userShippingAddress : String = "0"
    }
}
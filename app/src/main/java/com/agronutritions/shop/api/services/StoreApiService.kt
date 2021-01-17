package com.agronutritions.shop.api.services

import com.agronutritions.shop.api.response.*
import com.roadmate.app.api.response.AppVersionMaster
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StoreApiService {

    @GET("mobile_banner")
    suspend fun getBannerList(): Response<PagerData>

    @GET("category")
    suspend fun getCategoryList(): Response<CategoryData>

    @POST("subcategory")
    suspend fun getsubCategoryList(@Body jsonRequest : RequestBody): Response<SubCategoryData>

    @POST("product")
    suspend fun getProductsList(@Body jsonRequest : RequestBody): Response<ProductData>

    @POST("product_del")
    suspend fun getProductDetails(@Body jsonRequest : RequestBody): Response<ProductData>

    ////////////////////////////////////////////////////////////////////////////////////////////////



    @POST("pincode_list")
    suspend fun getPinCodeList(@Body jsonRequest : RequestBody): Response<PinData>

    @POST("placeorder")
    suspend fun insertOrder(@Body jsonRequest : RequestBody): Response<CommonResponse>

    ////////////////////////////////////////////////////////////////////////////////////////

//    @POST("register")
//    suspend fun signupUser(@Body jsonRequest : RequestBody): Response<SignupResponseData>

    @POST("newregister")
    suspend fun signupUser(@Body jsonRequest : RequestBody): Response<SignupResponseData>

    @POST("completeprofile")
    suspend fun completeProfile(@Body jsonRequest : RequestBody): Response<CompleteProfileResponseData>

    @POST("bankdetails")
    suspend fun bankDetails(@Body jsonRequest : RequestBody): Response<BankDetailsResponseData>

    @POST("login")
    suspend fun loginUser(@Body jsonRequest : RequestBody): Response<LoginResponseData>

    @POST("sendotp")
    suspend fun loginUserOTP(@Body jsonRequest : RequestBody): Response<LoginResponseData>

    @POST("otpchecking")
    suspend fun otpChecking(@Body jsonRequest : RequestBody): Response<OTPVerifyData>

    @POST("loginotpcheck")
    suspend fun otpCheckingLogin(@Body jsonRequest : RequestBody): Response<OTPVerifyData>
    ///////////////////////////////////////////////////////////////////////////////////////////

    @POST("myorders")
    suspend fun getOrderHistoryMaster(@Body jsonRequest : RequestBody): Response<OrderHistoryData>

    @POST("order_single_dets")
    suspend fun getOrderHistoryTrans(@Body jsonRequest : RequestBody): Response<OrderTransHistoryData>

    /////////
    @POST("geneologytree")
    suspend fun getgeneologyMaster(@Body jsonRequest : RequestBody): Response<GeneologyDataModel>

    @POST("wallethis")
    suspend fun getWalletMaster(@Body jsonRequest : RequestBody): Response<WalletDataModel>

    @POST("viewbankdetails")
    suspend fun getBankMaster(@Body jsonRequest : RequestBody): Response<BankdetailsDataModel>

    @POST("sponserprofile")
    suspend fun getSponserMaster(@Body jsonRequest : RequestBody): Response<SponserProfileDataModel>

    @GET("mobilemenu")
    suspend fun getReportMenusMaster(): Response<ReportMenusDataModel>

    @POST("myrefid")
    suspend fun getReferralID(@Body jsonRequest : RequestBody): Response<GetRefIdDataModel>

    @POST("checksponser")
    suspend fun getCheckReferralUser(@Body jsonRequest : RequestBody): Response<CheckReferralDataModel>

    @POST("totalwallet")
    suspend fun getWalletAmountView(@Body jsonRequest : RequestBody): Response<WalletAmtViewDataModel>

    //profile section

    @POST("myprofile")
    suspend fun getProfileiew(@Body jsonRequest : RequestBody): Response<ProfileViewDataModel>

    @POST("editprofile")
    suspend fun editProfileiew(@Body jsonRequest : RequestBody): Response<ProfileEditDataModelResp>
    //bank details edit\\
    @POST("bankdetails")
    suspend fun editBankDetails(@Body jsonRequest : RequestBody): Response<BankEditDataModelResp>

    @POST("deliveraddress")
    suspend fun viewDeliveryAddress(@Body jsonRequest : RequestBody): Response<DeliveryAddressDataModel>

    //delivery address edit
    @POST("editdeladdress")
    suspend fun editDeliveryDetails(@Body jsonRequest : RequestBody): Response<DeliveryEditDataModelResp>

    //rewards
    @GET("rewards")
    suspend fun getRewardsMaster(): Response<RewardsDataModel>
    @POST("myrewards")
    suspend fun getMyRewardsMaster(@Body jsonRequest : RequestBody): Response<RewardsDataModel>
//get
    @GET("states")
    suspend fun getstatesMaster(): Response<StateDataModel>

    @GET("versioncheck")
    suspend fun getAppVersionFromServer(): Response<AppVersionMaster>

    @GET("allproducts")
    suspend fun getAllProductsList(): Response<ProductData>


}
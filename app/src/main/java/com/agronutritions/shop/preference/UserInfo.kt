package com.agronutritions.shop.preference

import com.agronutritions.shop.constants.SharedPrefereceConstants

class UserInfo {
    var uid by Preference(SharedPrefereceConstants.PREF_UID, "")
    var name by Preference(SharedPrefereceConstants.PREF_UNAME, "")
    var mob by Preference(SharedPrefereceConstants.PREF_UMOB, "")
    var email by Preference(SharedPrefereceConstants.PREF_UEMAIL, "")
    var refBy by Preference(SharedPrefereceConstants.PREF_UREF, "")
    var rid by Preference(SharedPrefereceConstants.PREF_UREFID, "")

    //other fields
    var addresss by Preference(SharedPrefereceConstants.PREF_ADDRESS, "")
    var country by Preference(SharedPrefereceConstants.PREF_COUNTRY, "")
    var states by Preference(SharedPrefereceConstants.PREF_STATE, "")
    var city by Preference(SharedPrefereceConstants.PREF_CITY, "")
    var pincodes by Preference(SharedPrefereceConstants.PREF_PINCODE, "")

    var isProfileCompleted by Preference(SharedPrefereceConstants.PREF_PROFILE_COMPLETED, false)
    var isBankInfoCompleted by Preference(SharedPrefereceConstants.PREF_BANK_INFO_COMPLETED, false)
    var isTermsAccepted by Preference(SharedPrefereceConstants.PREF_TERMS_ACCEPTED, false)
}
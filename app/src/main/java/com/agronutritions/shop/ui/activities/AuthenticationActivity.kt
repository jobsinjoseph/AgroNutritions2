package com.agronutritions.shop.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.agronutritions.shop.R
import com.agronutritions.shop.constants.FragmentConstants
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.fragments.*
import kotlinx.android.synthetic.main.toolbar_main.*

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (UserInfo().uid.isNotEmpty() || UserInfo().mob.isNotEmpty()) {
            if (UserInfo().isProfileCompleted && UserInfo().isBankInfoCompleted && UserInfo().isTermsAccepted) {
                startActivity(Intent(this, Home::class.java).putExtra("fromlogin", true))
                this.finish()
            }else if (!UserInfo().isProfileCompleted){
                openCompleteProfileFragment()
            }else if (!UserInfo().isBankInfoCompleted){
                openBankDetailsFragment()
            }else if (!UserInfo().isTermsAccepted){
                openTermsNConditionFragment()
            }
        }else {
            openLoginFragment()
        }

    }

    fun setFragment(targetFragment: Fragment, fragmentTag: String?, bundle: Bundle?, addToBackStack: Boolean) {
        val transaction =
            supportFragmentManager.beginTransaction()
        if (bundle != null) {
            targetFragment.arguments = bundle
        }
        transaction.replace(R.id.container, targetFragment, fragmentTag)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag)
        } else {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun openLoginFragment(){
        setFragment(LoginFragment(), FragmentConstants.LOGIN_FRAGMENT, null, false)
    }

    fun openSignUpFragment(){
        setFragment(SignupFragment(), FragmentConstants.SIGNUP_FRAGMENT, null, true)
    }

    fun openOTPFragment(bundle: Bundle? = null){
        setFragment(OTPFragment(), FragmentConstants.OTP_FRAGMENT, bundle, true)
    }

    fun openBankDetailsFragment(bundle: Bundle? = null){
        setFragment(BankDetailsFragment(), FragmentConstants.COMPLETE_BANK_FRAGMENT, bundle, true)
    }

    fun openTermsNConditionFragment(bundle: Bundle? = null){
        setFragment(TermsNConditionFragment(), FragmentConstants.COMPLETE_TERMS_FRAGMENT, bundle, true)
    }

    fun openCompleteProfileFragment(bundle: Bundle? = null){
        //startActivity(Intent(this, Home::class.java))
       // this.finish()
        setFragment(CompleteProfileFragment(), FragmentConstants.COMPLETE_PROFILE_FRAGMENT, bundle, true)
    }

    fun setToolbar(isVisible: Boolean, titleMain: String?, titleSub: String?,
                           isMain : Boolean, showBack: Boolean) {
        if(showBack){
            toolbarBack.visibility = View.VISIBLE
        } else
            toolbarBack.visibility = View.GONE

        toolbar_title_sub2.visibility = View.GONE
        cust_icon.visibility = View.GONE
        if (isVisible) {
//            tbSub1.visibility = View.GONE
            if (isMain) {
                tbMain.visibility = View.VISIBLE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title.text = titleMain
                } else {
                    toolbar_title.text = getString(R.string.app_name)
                }
//                if (titleSub!!.isNotEmpty()) {
//                    toolbar_title_sub.text = titleSub
//                } else {
//                    toolbar_title.text = getString(R.string.app_name)
//                }

//                if (UserInfo().uid.isNotEmpty()){
//                    logout1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logout))
//                    logout1.setColorFilter(ContextCompat.getColor(this, R.color.check_out_btn_color), android.graphics.PorterDuff.Mode.SRC_IN);
//                }else{
//                    logout1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login))
//                    logout1.setColorFilter(ContextCompat.getColor(this, R.color.check_out_btn_color), android.graphics.PorterDuff.Mode.SRC_IN);
//                }

            }else{
//                tbMain.visibility = View.GONE
//                tbSub1.visibility = View.VISIBLE
                tbMain.visibility = View.VISIBLE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title2.text = titleMain
                } else {
                    toolbar_title.text = getString(R.string.app_name)
                }
//                if (titleSub!!.isNotEmpty()) {
//                    toolbar_title_sub2.text = titleSub
//                } else {
//                    toolbar_title_sub2.visibility = View.GONE
//                }
//                if (isCartView){
//                    cust_icon.visibility = View.VISIBLE
//                }else{
//                    cust_icon.visibility = View.GONE
//                }
            }
        } else {
            tbCommon.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        finish()
    }

}
package com.agronutritions.shop.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agronutritions.shop.R
import com.agronutritions.shop.preference.UserInfo
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.toolbar_main.*

open class BaseActivity : AppCompatActivity() {

    private fun setListeners() {
//        toolbarCustIcon.setOnClickListener(this)
//        rerset.setOnClickListener(this)
//        customToolbarSearch.setOnClickListener(this)
//        customToolbarBack.setOnClickListener(this)
//        logout.setOnClickListener(this)
//        toolbar_title.setOnClickListener(this)
//        toolbar_title2.setOnClickListener(this)
//        toolbarBack.setOnClickListener(this)
    }

    protected fun moveToScreen(targetActivity: Class<*>?, bundle: Bundle?, isFinish: Boolean) {
        val intent = Intent()
        intent.setClass(this, targetActivity!!)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
        if (isFinish) {
            finish()
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

    companion object{
        var hud: KProgressHUD? = null

        fun showProgress(context: Context?, message: String?) {
            hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()
        }

        fun dismissProgress() {
            try {
                hud!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    protected fun setToolbar(isVisible: Boolean, titleMain: String?, titleSub: String?, isMain : Boolean, isCartView: Boolean) {
        setListeners()
        if (isVisible) {
            if (isMain) {
                tbMain.visibility = View.VISIBLE
                tbSub1.visibility = View.GONE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title.text = titleMain
                } else {
                    toolbar_title.text = getString(R.string.app_name)
                }
                if (titleSub!!.isNotEmpty()) {
                    toolbar_title_sub.text = titleSub
                } else {
                    toolbar_title.text = getString(R.string.app_name)
                }

                if (UserInfo().uid.isNotEmpty()){
                    logout1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.logout))
                    logout1.setColorFilter(ContextCompat.getColor(this, R.color.check_out_btn_color), android.graphics.PorterDuff.Mode.SRC_IN);
                }else{
                    logout1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login))
                    logout1.setColorFilter(ContextCompat.getColor(this, R.color.check_out_btn_color), android.graphics.PorterDuff.Mode.SRC_IN);
                }

            }else{
                tbMain.visibility = View.GONE
                tbSub1.visibility = View.VISIBLE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title2.text = titleMain
                } else {
                    toolbar_title.text = "Agro Nutritions"
                }
                if (titleSub!!.isNotEmpty()) {
                    toolbar_title_sub2.text = titleSub
                } else {
                    toolbar_title_sub2.visibility = View.GONE
                }
                if (isCartView){
                    cust_icon.visibility = View.VISIBLE
                }else{
                    cust_icon.visibility = View.GONE
                }
            }
        } else {
            tbCommon.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_base_activity)
        setListeners()
    }

    /*override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.customToolbarBack -> {
                onBackPressed()
            }
            R.id.toolbarCustIcon -> {
            }
            R.id.rerset -> {
            }
        }
    }*/
}
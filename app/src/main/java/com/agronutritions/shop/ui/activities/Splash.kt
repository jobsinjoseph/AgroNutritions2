package com.agronutritions.shop.ui.activities

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.BuildConfig
import com.agronutritions.shop.R
import com.agronutritions.shop.api.manager.APIManager
import com.agronutritions.shop.api.services.StoreApiService
import com.agronutritions.shop.app.App
import com.agronutritions.shop.preference.UserInfo
import com.roadmate.app.api.response.AppVersionMaster
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch
import retrofit2.Response

class Splash : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        versionInfo.text = "v" + App.mApp.getAppVersion()

    }

    override fun onResume() {
        super.onResume()
        checkAppVersion()
    }

    private fun startOver(){
        Handler().postDelayed({
            if (UserInfo().uid.isNotEmpty() || UserInfo().mob.isNotEmpty()){
                startActivity(Intent(this, AuthenticationActivity::class.java))
            } else {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
            this.finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    private fun checkAppVersion(){
        lifecycleScope.launch {
            try {
                val response = APIManager.call<StoreApiService, Response<AppVersionMaster>> {
                    getAppVersionFromServer()
                }
                if (response.isSuccessful && !response.body()?.data!!.isNullOrEmpty()){
                    val serverAppVersion = response.body()?.data!![0];
                    if (serverAppVersion.version_code.toInt() > BuildConfig.VERSION_CODE && serverAppVersion.version_name != getAppVersionName()){
                        promptUpdate(serverAppVersion.version_name, getAppVersionName())
                    }else{
                        startOver()
                    }
                }
            } catch (e: Exception) {
                startOver()
            }
        }
    }

    private fun getAppVersionName(): String{
        var versionName = "0";
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return  versionName;
    }

    private fun promptUpdate(newVersion: String, oldversion: String) {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Update available!")
            .setMessage("You are using an out dated version(v$oldversion) of ${getString(R.string.app_name)}! An updated version(v$newVersion) available in Play Store.")
            .setPositiveButton("Update") { _, _ ->
                val appPackageName = packageName

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            .show()
    }
}
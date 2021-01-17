package com.agronutritions.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.agronutritions.shop.R
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.activities.AuthenticationActivity
import com.agronutritions.shop.ui.activities.Home
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.Utils
import kotlinx.android.synthetic.main.fragment_terms_and_conditions_details.*
import kotlinx.coroutines.launch

class TermsNConditionFragment:BaseFragment(), DialogInteractionListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_terms_and_conditions_details, container, false)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuthenticationActivity).setToolbar(true, "Terms and Conditions","",
            isMain = false,
            showBack = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_join.setOnClickListener {
            if(checkBox3.isChecked){
                val message = "Welcome to " + getString(R.string.app_name) +"!\nComplete the process by making your first purchase"
                Utils.showAlertOk(requireContext(), "", message, this, 101)
            } else {
                Toast.makeText(requireContext(), "Please accept terms and condition.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processAcceptTermsNCondition(){
        lifecycleScope.launch {

        }
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 101){
            UserInfo().isTermsAccepted = true
            startActivity(Intent(activity, Home::class.java).putExtra("fromlogin", false))
            requireActivity().finish()
        }
    }

    override fun onNegativeResponse(id: Int) {
        TODO("Not yet implemented")
    }

}
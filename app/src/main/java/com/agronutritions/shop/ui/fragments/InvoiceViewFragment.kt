package com.agronutritions.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agronutritions.shop.R
import kotlinx.android.synthetic.main.invoice_view_fragment.*


class InvoiceViewFragment : BaseFragment(){
    var ord_mas_id = ""
    private val postUrl = "https://agronutritions.com/public/print_invoice/"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invoice_view_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews(){
        ord_mas_id = arguments!!.getString("order_id")!!
        webView.settings.javaScriptEnabled = true;
        webView.loadUrl(postUrl);
        webView.isHorizontalScrollBarEnabled = false;
        webView.settings.setSupportZoom(true);
        webView.settings.builtInZoomControls = true;
        webView.settings.displayZoomControls = true;
        webView.loadUrl(postUrl+ord_mas_id);
    }
}

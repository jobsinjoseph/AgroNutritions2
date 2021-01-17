package com.agronutritions.shop.ui.activities
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronutritions.shop.R
import com.agronutritions.shop.adapters.PIncodeAdapter
import com.agronutritions.shop.app.App
import com.agronutritions.shop.app.Session
import com.agronutritions.shop.app.Session.Companion.lastPaymentTransactionId
import com.agronutritions.shop.app.Session.Companion.pinCodeItemsList
import com.agronutritions.shop.constants.BroadcastConstants
import com.agronutritions.shop.constants.FragmentConstants
import com.agronutritions.shop.log.AppLogger
import com.agronutritions.shop.model.OrderProductData
import com.agronutritions.shop.preference.UserInfo
import com.agronutritions.shop.ui.fragments.*
import com.agronutritions.shop.utils.DialogInteractionListener
import com.agronutritions.shop.utils.PincodeListener
import com.agronutritions.shop.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.layout_base_activity.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.coroutines.launch
import org.json.JSONObject


class Home : BaseActivity(), View.OnClickListener, PincodeListener, DialogInteractionListener, PaymentResultListener {

    private lateinit var pIncodeAdapter: PIncodeAdapter
    var behavior: BottomSheetBehavior<*>? = null
    private var doubleBackToExitPressedOnce = false
    private lateinit var menuView: BottomNavigationMenuView
    private lateinit var itemCartView: BottomNavigationItemView
    private lateinit var notificationBadge: View
    private lateinit var badgeText: TextView

    fun updateCountFlag(count: String){
        badgeText.text = count
        if (count.toInt() > 0) {
            notificationBadge.visibility = View.VISIBLE
        }else{
            notificationBadge.visibility = View.GONE
        }
    }

    fun dbAddProductToCart(orderProductData: OrderProductData){
        App.mApp.getDBLocked { db ->
            db?.getCartDao()?.let {
                lifecycleScope.launch {
                    showProgress(this@Home, "Refreshing..")
                    it.insertOrderProduct(
                        OrderProductData(
                            orderProductData.pdt_id,
                            orderProductData.pdt_name,
                            orderProductData.pdt_cat_id,
                            orderProductData.pdt_price,
                            orderProductData.pdt_qty,
                            orderProductData.pdt_total_price,
                            orderProductData.pdt_img
                        )
                    )
                    dismissProgress()
                }
            }
        }
    }

    fun dbRemoveProductFromCart(productId: String){
        App.mApp.getDBLocked { db ->
            db?.getCartDao()?.let {
                lifecycleScope.launch {
                    showProgress(this@Home, "Refreshing..")
                    it.removeProduct(productId)
                    dismissProgress()
                }
            }
        }
    }

    fun dbEmptyMyCart(){
        App.mApp.getDBLocked { db ->
            db?.getCartDao()?.let {
                lifecycleScope.launch {
                    showProgress(this@Home, "Refreshing..")
                    it.emptyCart()
                    clearOrderPreferences()
                    onHomeTabSelectedBottomNav()
                    dismissProgress()
                }
            }
        }
    }

    private fun dbGetAllProductsFromCart(){
        lifecycleScope.launch {
            showProgress(this@Home, "Loading cart..")
            Session.orderPrdtList = App.mApp.getDbInstance().getCartDao().getCartProducts() as ArrayList<OrderProductData>
            intent.extras?.let {
                if (it.containsKey("fromlogin")){
                    if (it["fromlogin"] as Boolean){
                        onHomeTabSelectedBottomNav()
                    }else{
                        onSubCategoryNav(Bundle(), "")
                    }
                }
            }?: run {
                onSubCategoryNav(Bundle(), "")}
            dismissProgress()
        }
    }

    private fun executeAppDoubleTapExit() {
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_LONG).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun clearOrderPreferences(){
        Session.orderPrdtList.clear()
        Session.orderPaymentModeId = "0"
        Session.orderPaymentInitiated = false
    }

    private fun onHomeTabSelectedBottomNav(){

        setToolbar(
            true, Session.userLocationMain, Session.userLocationSub,
            isMain = true,
            isCartView = false
        )

        indicator1.visibility = View.VISIBLE
        indicator2.visibility = View.INVISIBLE
        indicator3.visibility = View.INVISIBLE
        indicator4.visibility = View.INVISIBLE

        setFragment(HomeFragment(), FragmentConstants.HOME_FRAGMENT, null, false)
        navigationView.menu.findItem(R.id.nav_home).isChecked = true;
    }

    fun onSubCategoryNav(bundle: Bundle, titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )

        setFragment(
            HomeSubCatFragment(),
            FragmentConstants.HOME_SUB_FRAGMENT, bundle, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }

    fun setToolBarTitle(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
    }

    fun onProductdetailsNav(bundle: Bundle, titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            ProductDetailsFragment(),
            FragmentConstants.PRODUCT_DETAILS_FRAGMENT, bundle, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }



    ///////////////////////////////////////////////////////////////////////////////
    fun onCartTabSelectedBottomNav(){
        setToolbar(
            true, "My Cart", "",
            isMain = false,
            isCartView = true
        )
        setFragment(CartFragment(), FragmentConstants.CART_FRAGMENT, null, false)
        navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }

    private fun onHistoryTabSelectedBottomNav(){

        setToolbar(
            true, "Reports", "",
            isMain = false,
            isCartView = false
        )

        indicator1.visibility = View.INVISIBLE
        indicator2.visibility = View.INVISIBLE
        indicator3.visibility = View.VISIBLE
        indicator4.visibility = View.INVISIBLE

        setFragment(ReportsFragment(), FragmentConstants.REPORTS_FRAGMENT, null, false)
        navigationView.menu.findItem(R.id.nav_history).isChecked = true;
    }

    fun onProfileTabSelectedBottomNav(){
        setToolbar(
            true, "My Profile", "",
            isMain = false,
            isCartView = false
        )

        indicator1.visibility = View.INVISIBLE
        indicator2.visibility = View.INVISIBLE
        indicator3.visibility = View.INVISIBLE
        indicator4.visibility = View.VISIBLE

        setFragment(ProfileFragment(), FragmentConstants.PROFILE_FRAGMENT, null, false)
        navigationView.menu.findItem(R.id.nav_profile).isChecked = true;
    }

    fun onMyOrderNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            HistoryFragment(),
            FragmentConstants.HISTORY_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onGeneologyNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            GeneologysFragment(),
            FragmentConstants.GENEO_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }

    fun onWalletNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            WalletsFragment(),
            FragmentConstants.WALLET_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onBankDetNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            BankDetailsRepoFragment(),
            FragmentConstants.BANK_DET_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onSponserprofNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            SponserProfileRepoFragment(),
            FragmentConstants.BANK_DET_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onWalletAmtViewNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            ViewWalletAmountFragment(),
            FragmentConstants.WALLETAMT_DET_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onRewardsViewNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            RewardsFragment(),
            FragmentConstants.REWARDS_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onMyRewardsViewNav(titles: String){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            MyRewardsFragment(),
            FragmentConstants.MY_REWARDS_FRAGMENT, null, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }
    fun onMyInvoiceViewNav(titles: String,bundle: Bundle){
        setToolbar(
            true, titles, "",
            isMain = false,
            isCartView = false
        )
        setFragment(
            InvoiceViewFragment(),
            FragmentConstants.INVOICE_FRAGMENT, bundle, false
        )
        //navigationView.menu.findItem(R.id.nav_cart).isChecked = true;
    }

    fun manageBottomBar(isVisible: Boolean){
        when {
            isVisible -> {
                navigationView.visibility = View.VISIBLE
                indicatorlay.visibility = View.GONE
            }
            else -> {
                navigationView.visibility = View.GONE
                indicatorlay.visibility = View.GONE
            }
        }
    }

    fun getPinCode(listener: PincodeListener){
        pincodeList.setHasFixedSize(true);
        pincodeList.layoutManager = LinearLayoutManager(this);

        if (pinCodeItemsList.isNotEmpty()){
            pIncodeAdapter = PIncodeAdapter(this@Home!!, pinCodeItemsList){ _, pin, pinId ->
                Session.userLocationSub = pin
                Session.pincodeId = pinId
                behavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                listener.onPincodeUpdated()
                var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container)
                if (fragment is CartFragment){
                    setToolbar(
                        true, Session.userLocationMain, Session.userLocationSub,
                        isMain = false,
                        isCartView = true
                    )
                }
            }
            pincodeList.adapter = pIncodeAdapter
            behavior!!.state = BottomSheetBehavior.STATE_EXPANDED;
        }
    }

    fun openLoginFragment(){
        setToolbar(
            true, "Login", "",
            isMain = false,
            isCartView = false
        )
        setFragment(LoginFragment(), FragmentConstants.LOGIN_FRAGMENT, null, true)
    }

    fun openSignUpFragment(){
        setToolbar(
            true, "Join", "",
            isMain = false,
            isCartView = false
        )
        setFragment(SignupFragment(), FragmentConstants.SIGNUP_FRAGMENT, null, true)
    }

    fun openSearchFragment(){
        setToolbar(
            true, "Search", "",
            isMain = false,
            isCartView = false
        )
        setFragment(SearchFragment(), FragmentConstants.SEARCH_FRAGMENT, null, true)
    }

    private fun removeUser(){
        UserInfo().uid = ""
        UserInfo().name = ""
        UserInfo().email = ""
        UserInfo().mob = ""
        UserInfo().isProfileCompleted = false
        UserInfo().isBankInfoCompleted = false
        UserInfo().isTermsAccepted = false
        logout1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login))
        Toast.makeText(this, "You have logged out!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    fun processRazorPayPayment(totalAMount: Double){
        val activity: Activity = this

        val co = Checkout()

        try {
            co.setImage(R.drawable.logo_rpay)
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name));
            options.put("description", "Order Invoice Payment");
            options.put("currency", "INR");
            options.put("amount", totalAMount * 100);
//            options.put("amount", 1 * 100);

            val preFill = JSONObject()
            preFill.put("email", UserInfo().email)
            preFill.put("contact", UserInfo().mob)

            options.put("prefill", preFill)

            co.open(activity, options);
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private fun registerReceiver(){
        val mIntentFilter = IntentFilter()
        mIntentFilter.addAction(BroadcastConstants.ACTION_USER_JOINED)
        LocalBroadcastManager.getInstance(this).registerReceiver(OnNotice(), mIntentFilter)
    }

    private fun initializeViews(){
        menuView = navigationView.getChildAt(0) as BottomNavigationMenuView
        itemCartView = menuView.getChildAt(1) as BottomNavigationItemView
        notificationBadge = LayoutInflater.from(this).inflate(
            R.layout.component_tabbar_badge,
            menuView,
            false
        );
        badgeText = notificationBadge.findViewById(R.id.notificationsBadgeTextView)
        itemCartView.addView(notificationBadge);
        notificationBadge.visibility = View.GONE

        ///hide menu
        //navigationView.menu.findItem(R.id.nav_history).isVisible = false;
    }

    private fun setListeners(){
        logout1.setOnClickListener(this)
        toolbar_title2.setOnClickListener(this)
        toolbarBack.setOnClickListener(this)
        rerset.setOnClickListener(this)
        toolbar_title.setOnClickListener(this)

        supportFragmentManager.addOnBackStackChangedListener {
            var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container)
            if (fragment is HomeFragment){
                setToolbar(
                    true, Session.userLocationMain, Session.userLocationSub,
                    isMain = true,
                    isCartView = false
                )
            }
        }

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        behavior = BottomSheetBehavior.from(bottom_sheet)

        initializeViews()

        setListeners()

        dbGetAllProductsFromCart()

        registerReceiver()
    }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.nav_home -> {
                        onHomeTabSelectedBottomNav()
                        return true
                    }
                    R.id.nav_cart -> {
                        if (Session.orderPrdtList.isNotEmpty()) {
                            onCartTabSelectedBottomNav()
                        } else {
                            Toast.makeText(applicationContext, "Cart is empty!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return true
                    }
                    R.id.nav_history -> {
                        onHistoryTabSelectedBottomNav()
                        return true
                    }
                    R.id.nav_profile -> {
                        onProfileTabSelectedBottomNav()
                        return true
                    }
                }
                return false
            }
        }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.toolbarBack -> {
                onBackPressed()
            }
            R.id.logout1 -> {
                if (UserInfo().uid.isNotEmpty()) {
                    Utils.showAlertWarning(
                        this,
                        resources.getString(R.string.app_name),
                        "Confirm Logout?",
                        this,
                        1
                    )
                } else {
                    openLoginFragment()


                }
            }
            R.id.rerset -> {
                Utils.showAlertWarning(
                    this,
                    resources.getString(R.string.app_name),
                    "Are you sure you want to remove all items from your cart?",
                    this,
                    2
                )
            }
            R.id.toolbar_title -> {
                getPinCode(this)
            }
        }
    }

    override fun onBackPressed() {
        val imm: InputMethodManager = getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        var view: View? = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container)
        var fragmentCart: CartFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.CART_FRAGMENT) as? CartFragment

        if (behavior?.state === BottomSheetBehavior.STATE_EXPANDED){
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }else if (fragment is CartFragment && fragmentCart?.behavior?.state === BottomSheetBehavior.STATE_EXPANDED){
            fragmentCart?.behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        else if (fragment is CartFragment || fragment is HistoryFragment || fragment is ProfileFragment ){
            onHomeTabSelectedBottomNav()
        }else if (fragment is HomeFragment){
            if (doubleBackToExitPressedOnce) {
                finish()
            } else {
                executeAppDoubleTapExit()
            }
        }else if (supportFragmentManager.backStackEntryCount > 0){
                supportFragmentManager.popBackStack()
        }else{
            onHomeTabSelectedBottomNav()
        }
    }

    override fun onPincodeUpdated() {
        setToolbar(
            true, Session.userLocationMain, Session.userLocationSub,
            isMain = true,
            isCartView = false
        )
    }

    override fun onPositiveResponse(id: Int) {
        if (id == 1){
            removeUser()
        }
        else if (id == 2){
            dbEmptyMyCart()
        }
    }

    override fun onNegativeResponse(id: Int) {
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        var fragment: CartFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.CART_FRAGMENT) as CartFragment
        try {
            fragment?.paymentFailed(p1!!)
        } catch (e: java.lang.Exception) {
            AppLogger.error("OnPaymentError", "Exception in onPaymentError - " + e.message)
            fragment?.paymentFailed("OOPS! Something went wrong. Payment failed.")
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        AppLogger.info("onPaymentSuccess", "Message:- $p0")
        lastPaymentTransactionId = p0!!
        var fragment: CartFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.CART_FRAGMENT) as CartFragment
        fragment?.uploadOrder(lastPaymentTransactionId)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        try {
            var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container)
            if (ev!!.action === MotionEvent.ACTION_DOWN && behavior?.state === BottomSheetBehavior.STATE_EXPANDED) {

                val outRect = Rect()
                bottom_sheet.getGlobalVisibleRect(outRect)
                if (!outRect.contains(
                        ev!!.rawX.toInt(),
                        ev!!.rawY.toInt()
                    )
                ) behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }else if (ev!!.action === MotionEvent.ACTION_DOWN && fragment is CartFragment){
                var fragment: CartFragment? = supportFragmentManager.findFragmentByTag(
                    FragmentConstants.CART_FRAGMENT
                ) as CartFragment
                if (fragment?.behavior?.state === BottomSheetBehavior.STATE_EXPANDED) {
                    val outRect = Rect()
                    bottom_sheet_cart.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(
                            ev!!.rawX.toInt(),
                            ev!!.rawY.toInt()
                        )
                    ) fragment?.behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(OnNotice())
    }

    inner class OnNotice: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action.equals(BroadcastConstants.ACTION_USER_JOINED) && Session.orderPaymentInitiated) {
                onCartTabSelectedBottomNav()
                var fragment: CartFragment? = supportFragmentManager.findFragmentByTag(
                    FragmentConstants.CART_FRAGMENT
                ) as CartFragment
                fragment!!.alertUpload()
            }
        }

    }
}
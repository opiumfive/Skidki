package com.iterika.marvel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.iterika.marvel.auth.AuthFragment
import com.iterika.marvel.auth.RegFragment
import com.iterika.marvel.auth.RestoreFragment
import com.iterika.marvel.cart.CartFragment
import com.iterika.marvel.catalog.CatalogFragment
import com.iterika.marvel.catalog.FilterFragment
import com.iterika.marvel.catalog.Product
import com.iterika.marvel.catalog.ProductFragment
import com.iterika.marvel.coupons.Coupon
import com.iterika.marvel.coupons.CouponDetFragment
import com.iterika.marvel.coupons.CouponsFragment
import com.iterika.marvel.navigation.*
import com.iterika.marvel.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

class MainActivity : AppCompatActivity() {

    val navigationMap = object : SupportAppNavigator(this, R.id.container) {

        override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? {
            return null
        }

        override fun createFragment(screenKey: String?, data: Any?): Fragment? {
            var result: Fragment? = null
            when (screenKey) {
                TAB_CATALOG ->  {
                    tabBarContainer.visibility = View.VISIBLE
                    result = CatalogFragment.newInstance()
                }
                TAB_COUPONS ->  {
                    tabBarContainer.visibility = View.VISIBLE
                    result = CouponsFragment.newInstance()
                }
                TAB_PROFILE ->  {
                    tabBarContainer.visibility = View.VISIBLE
                    result = ProfileFragment.newInstance()
                }
                PRODUCT ->      {
                    tabBarContainer.visibility = View.VISIBLE
                    result = ProductFragment.newInstance(data as Product)
                }
                AUTH ->         {
                    tabBarContainer.visibility = View.GONE
                    result = AuthFragment.newInstance(if (data != null) data as Boolean else false)
                }
                CART ->         {
                    tabBarContainer.visibility = View.VISIBLE
                    result = CartFragment.newInstance(if (data != null) data as Boolean else false)
                }
                COUPON ->       {
                    tabBarContainer.visibility = View.VISIBLE
                    result = CouponDetFragment.newInstance(data as Coupon)
                }
                RESTORE ->      {
                    tabBarContainer.visibility = View.GONE
                    result = RestoreFragment.newInstance()
                }
                REG ->          {
                    tabBarContainer.visibility = View.GONE
                    result = RegFragment.newInstance(if (data != null) data as Boolean else false)
                }
                FILTER ->       {
                    tabBarContainer.visibility = View.VISIBLE
                    result = FilterFragment.newInstance()
                }
            }

            return result
        }
    }

    val navigator: Cicerone<Router> by inject()
    val prefs: Prefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            navigator.router.newRootScreen(TAB_CATALOG)
        }

        tabBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_catalog -> navigator.router.newRootScreen(TAB_CATALOG)
                R.id.action_coupons -> navigator.router.newRootScreen(TAB_COUPONS)
                R.id.action_profile -> navigator.router.newRootScreen(TAB_PROFILE)
            }
            true
        }
    }

    fun setTabCatalog() {
        tabBar.selectedItemId = R.id.action_catalog
    }

    fun setTabCoupons() {
        tabBar.selectedItemId = R.id.action_coupons
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigator.navigatorHolder.setNavigator(navigationMap)
    }

    override fun onPause() {
        navigator.navigatorHolder.removeNavigator()
        super.onPause()
    }
}

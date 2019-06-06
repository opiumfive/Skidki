package com.iterika.marvel.cart

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.R
import com.iterika.marvel.utils.GridItemDecorator
import com.iterika.marvel.utils.dp2px
import kotlinx.android.synthetic.main.fragment_cart.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Cicerone
import com.iterika.marvel.catalog.Retail
import com.iterika.marvel.coupons.ChooseShopDialog
import com.iterika.marvel.coupons.CouponResult
import com.iterika.marvel.utils.message
import ru.terrakok.cicerone.Router
import org.koin.android.viewmodel.ext.android.viewModel
import com.google.gson.Gson
import com.iterika.marvel.Prefs
import com.iterika.marvel.catalog.Product
import com.iterika.marvel.coupons.Coupon
import com.iterika.marvel.coupons.ProductName
import com.iterika.marvel.navigation.AUTH
import com.iterika.marvel.navigation.COUPON
import com.iterika.marvel.utils.hideKbd

class CartFragment: Fragment() {

    val viewModel: CartViewModel by viewModel()
    val navigator: Cicerone<Router> by inject()
    val cartHolder: CartHolder by inject()
    val prefs: Prefs by inject()

    var chosenRetLogo: String? = null
    var chosenProducts: List<Product>? = null

    val couponsObs: Observer<CouponResult> by lazy {
        Observer<CouponResult> {
            val picts = mutableListOf<String>()
            val names = mutableListOf<ProductName>()
            var sum = 0

            chosenProducts?.forEach {
                sum += (it.discount?.toInt() ?: 0) * it.count
                picts.addAll(it.picts!!)
                names.add(ProductName(it.name!!))
            }

            navigator.router.navigateTo(COUPON, Coupon(
                barcode = it?.coupon,
                discount = sum.toString(),
                retailLogo = chosenRetLogo,
                images = picts,
                productNames = names,
                shouldGoToCoupons = true
            ))

            cartHolder.products.removeAll(chosenProducts!!)
            handleEmpty()
            chosenProducts = null
            adapter?.notifyDataSetChanged()
        }
    }

    var adapter: CartAdapter? = null

    var toCart: Boolean? = false

    companion object {
        private val ARG_CART = "cart"

        fun newInstance(toCart: Boolean = false): CartFragment {
            val fragment = CartFragment()
            val args = Bundle()
            args.putBoolean(ARG_CART, toCart)
            fragment.arguments = args
            return fragment
        }
    }

    fun handleEmpty() {
        if (cartHolder.products.isEmpty()) {
            empty.visibility = View.VISIBLE
            get.visibility = View.GONE
        } else {
            empty.visibility = View.GONE
            get.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toCart = arguments?.getBoolean(ARG_CART)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    fun updateSum() {
        var sum = 0

        cartHolder.products.forEach {
            sum += (it.discount?.toInt() ?: 0) * it.count
        }

        sumMinus.text = "${-sum} рублей"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.hideKbd()

        back.setOnClickListener { navigator.router.exit() }

        viewModel.checkoutData.observe(this, couponsObs)

        cart.layoutManager = GridLayoutManager(activity, 2)
        cart.addItemDecoration(GridItemDecorator(2, context?.dp2px(16) ?: 0))

        adapter = CartAdapter(cartHolder.products) { it, delete ->
            if (delete.not()) {
                adapter?.notifyItemChanged(it)
            } else {
                adapter?.prodList?.removeAt(it)
                adapter?.notifyItemRemoved(it)
            }
            updateSum()
            handleEmpty()
        }

        cart.adapter = adapter

        updateSum()

        handleEmpty()

        get.setOnClickListener {
            if (prefs.token.isEmpty()) {
                navigator.router.navigateTo(AUTH, true)
            } else {
                goCheckout()
            }
        }

        if (toCart == true) {
            toCart = false
            goCheckout()
        }
    }

    fun goCheckout() {
        val retails = mutableListOf<Retail>()

        cartHolder.products.forEach {
            it.retails?.forEach {
                val current = it
                if (retails.find({ it.id == current.retail }) == null ) {
                    retails.add(Retail(id = it.retail, logo = it.logo, pagetitle = ""))
                }
            }
        }

        if (retails.size == 1) {
            chosenProducts = cartHolder.products
            val products = chosenProducts?.map { CartItem(id = it.id?.toInt()!!, count = it.count) }
            val json = Gson().toJson(products)
            viewModel.checkout(json, retails[0].id!!)
        } else {
            ChooseShopDialog(context!!, retails) {
                val retId = it.id
                chosenRetLogo = it.logo
                chosenProducts = cartHolder.products
                    .filter { it.retails?.find { it.retail == retId } != null }
                val products = chosenProducts?.map { CartItem(id = it.id?.toInt()!!, count = it.count) }
                val json = Gson().toJson(products)
                viewModel.checkout(json, it.id!!)
            }.show()
        }
    }
}

data class CartItem(val id: Int, val count: Int)
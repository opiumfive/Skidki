package com.iterika.marvel.catalog

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import com.iterika.marvel.cart.CartHolder
import com.iterika.marvel.coupons.*
import com.iterika.marvel.navigation.CART
import com.iterika.marvel.navigation.COUPON
import com.iterika.marvel.utils.RecyclerItemDecorator
import com.iterika.marvel.utils.dp2px
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.view_dot.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class ProductFragment : Fragment() {

    val viewModel: CatalogViewModel by viewModel()
    val navigator: Cicerone<Router> by inject()
    val cartHolder: CartHolder by inject()

    private var product: Product? = null

    val fastCouponObs: Observer<CouponResult> by lazy {
        Observer<CouponResult> {
            navigator.router.navigateTo(COUPON, Coupon(
                barcode = it?.coupon,
                discount = product?.discount,
                retailLogo = product?.retails?.firstOrNull()?.logo,
                images = product?.picts,
                productNames = listOf(ProductName(product?.name ?: ""))
            ))
        }
    }

    companion object {

        private val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putParcelable(ARG_PRODUCT, product)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getParcelable(ARG_PRODUCT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fastCouponData.observe(this, fastCouponObs)

        back.setOnClickListener { navigator.router.exit() }

        imagesPager.adapter = FlowerImageAdapter(context, product?.picts ?: emptyList())

        if ((product?.picts?.size ?: 0) > 1) {
            viewPagerIndicator.visibility = View.VISIBLE
            viewPagerIndicator.adapter = DotAdapter(product?.picts?.size ?: 0, 0)
            viewPagerIndicator.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            viewPagerIndicator.addItemDecoration(RecyclerItemDecorator(rightOffset = context?.dp2px(8) ?: 0))
        } else {
            viewPagerIndicator.visibility = View.GONE
        }

        imagesPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(pos: Int) {
                viewPagerIndicator.adapter = DotAdapter(product?.picts?.size ?: 0, pos)
            }
        })

        name.text = product?.name?.trim()
        name2.text = product?.name?.trim()
        cat.text = product?.category
        GlideApp.with(context!!).load(product?.retails?.get(0)?.logo).into(retailImg)
        discount.text = "Максимальная скидка: ${product?.discount}\u20BD"
        expires.text = "Действительно до ${product?.retails?.get(0)?.expires}"

        fastCoupon.setOnClickListener {
            context?.let{
                val ctx = it
                GetFastCouponDialog(ctx) {
                    product?.retails
                        ?.map {
                            Retail(id = it.retail, logo = it.logo, pagetitle = "")
                        }?.let {
                            if (it.size == 1) {
                                viewModel.fastCoupon(product?.id!!, it[0].id!!)
                            } else {
                                ChooseShopDialog(ctx, it) {
                                    viewModel.fastCoupon(product?.id!!, it.id!!)
                                }.show()
                            }
                        }
                }.show()
            }
        }

        toCart.setOnClickListener {
            context?.let {
                AddCartDialog(it, product) {
                    val count = it
                    product?.let {
                        cartHolder.addOrMerge(it.apply { this.count = count } )
                    }
                }.show()
            }
        }

        cart.setOnClickListener { navigator.router.navigateTo(CART) }
    }
}

class DotAdapter(val count: Int, val pos: Int = 0): RecyclerView.Adapter<DotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_dot, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = count

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.dot.alpha = if (position == pos) 1.0f else 0.5f
        }
    }
}
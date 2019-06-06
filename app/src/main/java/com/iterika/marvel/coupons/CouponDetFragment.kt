package com.iterika.marvel.coupons

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import com.iterika.marvel.catalog.DotAdapter
import com.iterika.marvel.catalog.FlowerImageAdapter
import com.iterika.marvel.utils.RecyclerItemDecorator
import com.iterika.marvel.utils.dp2px
import kotlinx.android.synthetic.main.fragment_coupon.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.iterika.marvel.MainActivity


class CouponDetFragment : Fragment() {

    val navigator: Cicerone<Router> by inject()
    val viewModel: CouponsViewModel by viewModel()
    val couponsRepo: CouponsRepo by inject()

    private var coupon: Coupon? = null
    private var shouldGoToCouponsAfterClose = false
    val couponsObs: Observer<OrderDetails> by lazy { Observer<OrderDetails> { showDetails(it) } }

    companion object {

        private val ARG_COUPON = "coupon"

        fun newInstance(coupon: Coupon, toCoupons: Boolean = false): CouponDetFragment {
            val fragment = CouponDetFragment()
            val args = Bundle()
            args.putParcelable(ARG_COUPON, coupon)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coupon = arguments?.getParcelable(ARG_COUPON)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.couponDetailData.observe(this, couponsObs)

        shouldGoToCouponsAfterClose = coupon?.shouldGoToCoupons ?: false

        back.setOnClickListener {
            if (shouldGoToCouponsAfterClose) {
                (activity as? MainActivity)?.setTabCoupons()
            } else {
                navigator.router.exit()
            }
        }

        if (coupon?.id.isNullOrEmpty()) {
            couponsRepo.clearCache()
            discount.text = "-${coupon?.discount ?: 0} \u20BD"
            expires.visibility = if (coupon?.date != null) View.VISIBLE else View.GONE
            expires.text = "Действительно до ${coupon?.date}"

            code.text = coupon?.barcode

            imagesPager.adapter = FlowerImageAdapter(context, coupon?.images ?: emptyList())
            if ((coupon?.images?.size ?: 0) > 1) {
                viewPagerIndicator.visibility = View.VISIBLE
                viewPagerIndicator.adapter = DotAdapter(coupon?.images?.size ?: 0, 0)
                viewPagerIndicator.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                viewPagerIndicator.addItemDecoration(RecyclerItemDecorator(rightOffset = context?.dp2px(8) ?: 0))
            } else {
                viewPagerIndicator.visibility = View.GONE
            }

            imagesPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(pos: Int) {
                    viewPagerIndicator.adapter = DotAdapter(coupon?.images?.size ?: 0, pos)
                }
            })

            coupon?.productNames?.let { name.text = TextUtils.join(",", coupon?.productNames?.map { it.name?.trim() }) }
            GlideApp.with(context!!).load(coupon?.retailLogo).into(retailImg)

            showBarcode(coupon?.barcode)
        } else {
            viewModel.getDetail(coupon?.id!!)
        }
    }

    private fun showBarcode(bar: String?) {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(bar, BarcodeFormat.CODE_128, 400, 200, null)
            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, 400, 0, 0, w, h)
            GlideApp.with(context!!).load(bitmap).into(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDetails(it: OrderDetails?) {
        it?.let {
            discount.text = "-${coupon?.discount ?: 0} \u20BD"
            expires.visibility = if (coupon?.date != null) View.VISIBLE else View.GONE
            expires.text = "Действительно до ${coupon?.date}"
            code.text = it.barcode

            val pictos = it.items?.flatMap { it.picts ?: emptyList() }

            imagesPager.adapter = FlowerImageAdapter(context, pictos ?: emptyList())

            if ((pictos?.size ?: 0) > 1) {
                viewPagerIndicator.visibility = View.VISIBLE
                viewPagerIndicator.adapter = DotAdapter(pictos?.size ?: 0, 0)
                viewPagerIndicator.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                viewPagerIndicator.addItemDecoration(RecyclerItemDecorator(rightOffset = context?.dp2px(8) ?: 0))
            } else {
                viewPagerIndicator.visibility = View.GONE
            }

            imagesPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(pos: Int) {
                    viewPagerIndicator.adapter = DotAdapter(pictos?.size ?: 0, pos)
                    number.text = "Количество шт: ${it.items?.get(pos)?.count}"
                }
            })

            number.visibility = View.VISIBLE
            number.text = "Количество шт: ${it.items?.get(0)?.count}"

            coupon?.productNames?.let { name.text = TextUtils.join(",", coupon?.productNames?.map { it.name?.trim() }) }
            GlideApp.with(context!!).load(coupon?.retailLogo).into(retailImg)

            showBarcode(it.barcode)
        }
    }
}
package com.iterika.marvel.coupons

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import com.iterika.marvel.navigation.COUPON
import com.iterika.marvel.utils.DateFormatter
import kotlinx.android.synthetic.main.fragment_coupons.*
import kotlinx.android.synthetic.main.view_coupon.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class CouponsFragment : Fragment() {

    val viewModel: CouponsViewModel by viewModel()
    private var couponsAdapter: CouponsAdapter? = null
    val navigator: Cicerone<Router> by inject()
    val dateFormatter: DateFormatter by inject()

    val couponsObs: Observer<List<Coupon>> by lazy { Observer<List<Coupon>> { showCoupons(it) } }

    companion object {
        fun newInstance() = CouponsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coupons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        couponsRecycler.layoutManager = LinearLayoutManager(activity)

        couponsAdapter = CouponsAdapter(dateFormatter) { navigator.router.navigateTo(COUPON, it) }

        couponsRecycler.adapter = couponsAdapter
        couponsRecycler.setHasFixedSize(false)

        pullRefresh.setOnRefreshListener {
            pullRefresh.isRefreshing = false

            couponsAdapter?.clear()
            progress.visibility = View.VISIBLE
            viewModel.getCoupons(true)
        }

        viewModel.couponsData.observe(this, couponsObs)

        viewModel.getCoupons(false)
        progress.visibility = View.VISIBLE
    }

    private fun showCoupons(data: List<Coupon>?) {
        progress.visibility = View.INVISIBLE
        if (data != null) {
            couponsAdapter?.clearAndAddList(data)
            if (data.isEmpty()) {
                empty.visibility = View.VISIBLE
            } else {
                empty.visibility = View.INVISIBLE
            }
        } else {
            empty.visibility = View.VISIBLE
        }
    }
}

class CouponsAdapter(val dateFormatter: DateFormatter, private val itemClick: (Coupon?) -> Unit) : RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {

    var catList = mutableListOf<Coupon>()

    fun clear() {
        catList.clear()
        notifyDataSetChanged()
    }

    fun clearAndAddList(list: List<Coupon>) {
        catList.clear()
        catList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_coupon, parent, false
        ), itemClick
    )

    private fun getItem(position: Int) = catList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), if (position > 0) getItem(position - 1) else null)

    override fun getItemCount() = catList.size

    inner class ViewHolder(itemView: View, private val listener: (Coupon?) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Coupon?, prev: Coupon?) {
            GlideApp.with(itemView.context).load(cat?.retailLogo).into(itemView.magnolia)
            cat?.productNames?.let { itemView.name.text = TextUtils.join(",", cat.productNames.map { it.name?.trim() }) }
            itemView.date.text = dateFormatter.format(cat?.date)
            itemView.date.visibility = if (prev?.date != null && dateFormatter.format(prev.date) == dateFormatter.format(cat?.date)) View.GONE else View.VISIBLE
            itemView.dateTill.text = cat?.date
            itemView.discount.text = "-${cat?.discount ?: 0} \u20BD"
            itemView.type.text = when (cat?.status) {
                "closed" -> "Не активный"
                else -> ""
            }
            itemView.setOnClickListener { listener.invoke(cat) }
        }
    }
}
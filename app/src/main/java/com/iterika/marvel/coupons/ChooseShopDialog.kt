package com.iterika.marvel.coupons

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import com.iterika.marvel.R
import com.iterika.marvel.catalog.Retail
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import kotlinx.android.synthetic.main.dialog_choose_shop.*
import kotlinx.android.synthetic.main.view_shop_image.view.*

class ChooseShopDialog(context: Context, shops: List<Retail>, val listener: ((Retail) -> Unit)? = null): Dialog(context) {

    init {
        setContentView(R.layout.dialog_choose_shop)

        val adapter = ShopAdapter(shops) {
            it?.let { listener?.invoke(it) }
            dismiss()
        }

        shopRecycler.layoutManager = LinearLayoutManager(context)
        shopRecycler.adapter = adapter
    }
}

class ShopAdapter(private val shops: List<Retail>, private val itemClick: (Retail?) -> Unit): RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_shop_image, parent, false), itemClick)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(shops[position])

    override fun getItemCount() = shops.size

    inner class ViewHolder(itemView: View, itemClick: (Retail?) -> Unit): RecyclerView.ViewHolder(itemView) {
        fun bind(ret: Retail) {
            itemView.setOnClickListener { itemClick.invoke(ret) }
            GlideApp.with(itemView).load(ret.logo).into(itemView.img)
        }
    }
}
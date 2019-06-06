package com.iterika.marvel.catalog

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import kotlinx.android.synthetic.main.view_product.view.*

class ProductsAdapter(private val itemClick: (Product?, Int) -> Unit) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var prodList = mutableListOf<Product>()

    fun clear() {
        prodList.clear()
        notifyDataSetChanged()
    }

    fun addList(list: List<Product>) {
        prodList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearAndAddList(list: List<Product>) {
        prodList.clear()
        prodList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_product, parent, false), itemClick)

    private fun getItem(position: Int) = prodList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemCount() = prodList.size

    inner class ViewHolder(itemView: View, private val listener: (Product?, Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Product?) {
            GlideApp.with(itemView.context).load(cat?.picts?.firstOrNull()).fitCenter().into(itemView.image)
            itemView.title.text = cat?.name?.trim()
            itemView.discount.text = "Скидка: -${cat?.discount ?: 0} РУБ"
            itemView.setOnClickListener { listener.invoke(cat, 0) }

            itemView.coupon.setOnClickListener {
                listener.invoke(cat, 1)
            }

            itemView.cart.setOnClickListener {
                listener.invoke(cat, 2)
            }
        }
    }
}
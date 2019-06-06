package com.iterika.marvel.cart

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import com.iterika.marvel.catalog.Product
import kotlinx.android.synthetic.main.view_cart.view.*

class CartAdapter(
    val prodList: MutableList<Product>,
    val listener: ((Int, Boolean) -> Unit)
): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_cart, parent, false), listener)

    private fun getItem(position: Int) = prodList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(holder.adapterPosition))

    override fun getItemCount() = prodList.size

    inner class ViewHolder(itemView: View, val listener: ((Int, Boolean) -> Unit)) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Product?) {
            GlideApp.with(itemView.context).load(cat?.picts?.firstOrNull()).fitCenter().into(itemView.image)
            itemView.title.text = cat?.name?.trim()
            itemView.discount.text = "Скидка: -${cat?.discount ?: 0} РУБ"

            itemView.count.text = "${cat?.count} шт."

            itemView.plus.setOnClickListener {
                cat?.let { it.count++ }
                listener.invoke(adapterPosition, false)
            }

            itemView.minus.setOnClickListener {
                cat?.let {
                    if (it.count > 1) {
                        it.count--
                        listener.invoke(adapterPosition, false)
                    } else {
                        listener.invoke(adapterPosition, true)
                    }
                }
            }
        }
    }
}
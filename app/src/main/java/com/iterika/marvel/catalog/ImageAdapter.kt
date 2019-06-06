package com.iterika.marvel.catalog

import android.content.Context
import android.widget.LinearLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.view.View
import android.widget.ImageView
import com.iterika.marvel.R


class FlowerImageAdapter(val context: Context?, private val images: List<String>) : PagerAdapter() {

    override fun getCount() = images.size

    override fun isViewFromObject(view: View, obj: Any) = view === obj as LinearLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.view_image, container, false)

        val img = itemView.findViewById(R.id.img) as ImageView

        if (images.isNotEmpty()) {
            GlideApp.with(context).load(images[position]).into(img)
        }

        (container as ViewPager).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) = (container as ViewPager).removeView(obj as LinearLayout)
}
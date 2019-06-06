package com.iterika.marvel

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.utils.RecyclerItemDecorator
import com.iterika.marvel.utils.dp2px
import com.iterika.marvel.utils.drawable
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.android.synthetic.main.view_dot.view.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject


const val WELCOME_PAGES_COUNT = 3

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        viewPager.adapter = WelcomePageAdapter(supportFragmentManager)

        indicatorRecycler.adapter = DotsAdapter(WELCOME_PAGES_COUNT, 0)
        indicatorRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        indicatorRecycler.addItemDecoration(RecyclerItemDecorator(rightOffset = dp2px(8)))

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) = Unit
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) = Unit
            override fun onPageSelected(p0: Int) = indicatorRecycler.setAdapter(DotsAdapter(WELCOME_PAGES_COUNT, p0))
        })
    }
}

class WelcomePageFragment : Fragment() {

    val prefs: Prefs by inject()

    private var pos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pos = arguments?.getInt(ARG_POS) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    companion object {

        private const val ARG_POS = "pos"

        fun newInstance(pos: Int): WelcomePageFragment {
            val fragment = WelcomePageFragment()
            val args = Bundle()
            args.putInt(ARG_POS, pos)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (pos) {
            0 -> {
                image.setImageDrawable(context?.drawable(R.drawable.welcome1))
                title.text = "Выбирайте товары"
                subtitle.text = "Собирайте корзину товаров и смотрите как растет ваша выгода"
            }
            1 -> {
                image.setImageDrawable(context?.drawable(R.drawable.welcome2))
                title.text = "Получайте купоны"
                subtitle.text = "Получайте скидку на любой товар моментально или получите один купон на все товары в корзине"
            }
            2 -> {
                image.setImageDrawable(context?.drawable(R.drawable.welcome3))
                title.text = "Покажите купоны на кассе"
                subtitle.text = "В настоящее время скидки доступны только в торговой сети «Магнолия»"
                magnolia.visibility = View.VISIBLE
                toCatalog.visibility = View.VISIBLE
                toCatalog.setOnClickListener {
                    prefs.welcomePassed = true
                    activity?.startActivity<MainActivity>()
                    activity?.finish()
                }
            }
        }
    }
}

class WelcomePageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = WelcomePageFragment.newInstance(position)
    override fun getCount() = WELCOME_PAGES_COUNT
    override fun getPageTitle(position: Int) = ""
}

class DotsAdapter(private val mCount: Int, private val mCurrentId: Int) :
    RecyclerView.Adapter<DotsAdapter.ViewHolderImpl>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderImpl(LayoutInflater.from(parent.context).inflate(R.layout.view_dot, parent, false))

    override fun onBindViewHolder(holder: ViewHolderImpl, position: Int) = holder.bind(position)

    override fun getItemCount() = mCount

    inner class ViewHolderImpl(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) = itemView.dot.setAlpha(if (position == mCurrentId) 1.0f else 0.5f)
    }
}
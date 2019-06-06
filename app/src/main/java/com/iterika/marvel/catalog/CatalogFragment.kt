package com.iterika.marvel.catalog

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.iterika.marvel.R
import com.iterika.marvel.cart.CartHolder
import com.iterika.marvel.coupons.*
import com.iterika.marvel.navigation.CART
import com.iterika.marvel.navigation.COUPON
import com.iterika.marvel.navigation.FILTER
import com.iterika.marvel.navigation.PRODUCT
import com.iterika.marvel.utils.*
import kotlinx.android.synthetic.main.fragment_catalog.*
import kotlinx.android.synthetic.main.view_category.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router


class CatalogFragment : Fragment() {

    val viewModel: CatalogViewModel by viewModel()
    val navigator: Cicerone<Router> by inject()
    val cartHolder: CartHolder by inject()

    private var catsAdapter: CategoriesAdapter? = null
    private var productsAdapter: ProductsAdapter? = null
    val currentProducts = mutableListOf<Product>()
    private var couponProduct: Product? = null

    val catsObs: Observer<List<Category>> by lazy { Observer<List<Category>> { showCats(it) } }
    val catalogObs: Observer<List<Product>> by lazy { Observer<List<Product>> { addProducts(it) } }
    val searchObs: Observer<List<Product>> by lazy { Observer<List<Product>> { showProducts(it) } }
    val fastCouponObs: Observer<CouponResult> by lazy {
        Observer<CouponResult> {
            navigator.router.navigateTo(COUPON, Coupon(
                barcode = it?.coupon,
                discount = couponProduct?.discount,
                retailLogo = couponProduct?.retails?.firstOrNull()?.logo,
                images = couponProduct?.picts,
                productNames = listOf(ProductName(couponProduct?.name ?: ""))
            ))
        }
    }

    companion object {
        fun newInstance() = CatalogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateSum()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        productsRecycler.layoutManager = GridLayoutManager(activity, 2)
        productsRecycler.addItemDecoration(GridItemDecorator(2, context?.dp2px(16) ?: 0))

        val snapHelper = StartSnapHelper()
        snapHelper.attachToRecyclerView(categoriesRecycler)

        catsAdapter = CategoriesAdapter {
            it?.let {
                viewModel.repo.catsFilter.clear()
                viewModel.repo.catsFilter.addAll(listOf(it))
                viewModel.currentCatalogOffset = 0
                productsAdapter?.clear()
                currentProducts.clear()
                viewModel.repo.currentProducts.clear()
                progress.visibility = View.VISIBLE
                viewModel.getCatalog()
            }
        }

        pullRefresh.setOnRefreshListener {
            pullRefresh.isRefreshing = false
            clearAndRequest()
        }

        cart.setOnClickListener { navigator.router.navigateTo(CART) }

        categoriesRecycler.adapter = catsAdapter

        productsAdapter = ProductsAdapter { product, i ->
            when (i) {
                0 -> navigator.router.navigateTo(PRODUCT, product) // usual click
                1 -> {
                    context?.let{
                        val ctx = it
                        GetFastCouponDialog(ctx) {
                            product?.retails
                                ?.map {
                                    Retail(id = it.retail, logo = it.logo, pagetitle = "")
                                }?.let {
                                    couponProduct = product
                                    if (it.size == 1) {
                                        viewModel.fastCoupon(product.id!!, it[0].id!!)
                                    } else {
                                        ChooseShopDialog(ctx, it) {
                                            viewModel.fastCoupon(product.id!!, it.id!!)
                                        }.show()
                                    }
                                }
                        }.show()
                    }
                }
                2 -> { // cart
                    context?.let {
                        AddCartDialog(it, product) {
                            val count = it
                            product?.let {
                                cartHolder.addOrMerge(it.apply { this.count = count } )
                                updateSum()
                            }
                        }.show()
                    }
                }
            }
        }

        productsRecycler.adapter = productsAdapter

        // retrieve data

        viewModel.catsData.observe(this, catsObs)
        viewModel.catalogData.observe(this, catalogObs)
        viewModel.searchData.observe(this, searchObs)
        viewModel.fastCouponData.observe(this, fastCouponObs)

        search.setOnClickListener {
            searchView.visibility = View.VISIBLE
            usualView.visibility = View.GONE
            searchQuery.requestFocus()

            categoriesRecycler.visibility = View.GONE

            activity?.hideKbd()
        }

        filter.setOnClickListener { navigator.router.navigateTo(FILTER) }

        backSearch.setOnClickListener {
            searchView.visibility = View.GONE
            usualView.visibility = View.VISIBLE

            categoriesRecycler.visibility = View.VISIBLE

            showProducts(currentProducts)

            activity?.hideKbd()
        }

        searchQuery.onTextChangedDebounced { viewModel.search(it) }

        searchQuery.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                return if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.search(searchQuery.text.toString())
                    activity?.hideKbd()
                    true
                } else false
            }
        })

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if ((productsAdapter?.itemCount ?: 0) > 0
                && scrollView?.getChildAt(0)?.bottom == (scrollView.height + scrollView.scrollY)
            ) {
                progress.visibility = View.VISIBLE
                viewModel.getCatalog()
            }
        }

        viewModel.getCats(false)

        // if from backstack

        if (viewModel.repo.needToRefresh) {
            clearAndRequest()
        } else {
            if (currentProducts.isEmpty()) {
                progress.visibility = View.VISIBLE
                viewModel.getCatalog()
            } else {
                showProducts(currentProducts)
            }
        }
    }

    fun clearAndRequest() {
        viewModel.currentCatalogOffset = 0
        viewModel.repo.needToRefresh = false
        productsAdapter?.clear()
        currentProducts.clear()
        viewModel.repo.currentProducts.clear()
        progress.visibility = View.VISIBLE
        viewModel.getCatalog()

        catsAdapter?.clear()
        viewModel.getCats(true)
    }

    private fun addProducts(data: List<Product>?) {
        progress.visibility = View.INVISIBLE
        if (data != null) {
            productsAdapter?.addList(data)
            currentProducts.addAll(data)

            nothing.visibility = View.GONE
        }
    }

    private fun showProducts(data: List<Product>?) {
        progress.visibility = View.INVISIBLE
        if (data != null) {
            productsAdapter?.clearAndAddList(data)

            nothing.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showCats(data: List<Category>?) {
        if (data != null) {
            catsAdapter?.clearAndAddList(data)
        }
    }

    fun updateSum() {
        var sum = 0

        cartHolder.products.forEach {
            sum += (it.discount?.toInt() ?: 0) * it.count
        }

        cartSum.text = "${-sum} \u20BD"
    }

}

class CategoriesAdapter(private val itemClick: (Category?) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    var catList = mutableListOf<Category>()

    fun clear() {
        catList.clear()
        notifyDataSetChanged()
    }

    fun clearAndAddList(list: List<Category>) {
        catList.clear()
        catList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_category, parent, false
        ), itemClick
    )

    private fun getItem(position: Int) = catList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    override fun getItemCount() = catList.size

    inner class ViewHolder(itemView: View, private val listener: (Category?) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Category?) {

            itemView.container.background = itemView.context.drawable(
                when (adapterPosition % 3) {
                    0 -> R.drawable.category_bg
                    1 -> R.drawable.category_bg_2
                    else -> R.drawable.category_bg_3
                }
            )

            //GlideApp.with(itemView.context).load(cat?.image).into(itemView.image)
            itemView.text.text = cat?.pagetitle
            itemView.setOnClickListener { listener.invoke(cat) }
        }
    }
}
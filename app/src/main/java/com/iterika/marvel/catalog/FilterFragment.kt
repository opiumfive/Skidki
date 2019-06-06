package com.iterika.marvel.catalog

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.GlideApp
import com.iterika.marvel.R
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.view_category_filter.view.*
import kotlinx.android.synthetic.main.view_retail_filter.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class FilterFragment : Fragment() {

    val viewModel: CatalogViewModel by viewModel()
    val retailObs: Observer<List<Retail>> by lazy { Observer<List<Retail>> { showRetails(it) } }
    val catsObs: Observer<List<Category>> by lazy { Observer<List<Category>> { showCats(it) } }

    val navigator: Cicerone<Router> by inject()

    private var catsAdapter: CatAdapter? = null
    private var retailsAdapter: RetailAdapter? = null
    private var numberLoaded = 0

    companion object {
        fun newInstance() = FilterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.retailData.observe(this, retailObs)
        viewModel.catsData.observe(this, catsObs)

        back.setOnClickListener { navigator.router.exit() }

        retails.layoutManager = LinearLayoutManager(activity)
        cats.layoutManager = LinearLayoutManager(activity)

        catsAdapter = CatAdapter {
            it?.let {
                val cat = it
                if (catsAdapter?.chosenList?.find { cat.id == it.id } != null) {
                    catsAdapter?.chosenList?.remove(cat)
                } else {
                    catsAdapter?.chosenList?.add(cat)
                }

                allCats.isChecked = catsAdapter?.chosenList?.isEmpty() == true

                viewModel.repo.catsFilter.clear()
                viewModel.repo.catsFilter.addAll(catsAdapter?.chosenList ?: emptyList())
                catsAdapter?.notifyDataSetChanged()
                viewModel.repo.needToRefresh = true
            }
        }

        allCats.isChecked = catsAdapter?.chosenList?.isEmpty() == true

        allCats.setOnCheckedChangeListener { _, b ->
            if (b) {
                viewModel.repo.catsFilter.clear()
                catsAdapter?.chosenList?.clear()
                catsAdapter?.notifyDataSetChanged()
                viewModel.repo.needToRefresh = true
            }
        }

        retailsAdapter = RetailAdapter {
            it?.let {
                val retail = it
                if (retailsAdapter?.chosenList?.find { retail.id == it.id } != null) {
                    retailsAdapter?.chosenList?.remove(retail)
                } else {
                    retailsAdapter?.chosenList?.add(retail)
                }
                viewModel.repo.retailsFilter.clear()
                viewModel.repo.retailsFilter.addAll(retailsAdapter?.chosenList ?: emptyList())
                retailsAdapter?.notifyDataSetChanged()
                viewModel.repo.needToRefresh = true
            }
        }

        clear.setOnClickListener {
            viewModel.repo.retailsFilter.clear()
            viewModel.repo.catsFilter.clear()
            viewModel.repo.needToRefresh = true

            catsAdapter?.chosenList?.clear()
            retailsAdapter?.chosenList?.clear()
            allCats.isChecked = true

            retailsAdapter?.notifyDataSetChanged()
            catsAdapter?.notifyDataSetChanged()
        }

        retails.adapter = retailsAdapter
        cats.adapter = catsAdapter

        numberLoaded = 0
        progress.visibility = View.VISIBLE
        mainView.visibility = View.GONE
        viewModel.getRetails()
        viewModel.getCats(false)
    }

    private fun showRetails(data: List<Retail>?) {
        handleProgress()
        if (data != null) {
            retailsAdapter?.clearAndAddList(data, viewModel.repo.retailsFilter)
        }
    }

    private fun showCats(data: List<Category>?) {
        handleProgress()
        if (data != null) {
            catsAdapter?.clearAndAddList(data, viewModel.repo.catsFilter)
        }
    }

    private fun handleProgress() {
        if (++numberLoaded == 2) {
            progress.visibility = View.GONE
            mainView.visibility = View.VISIBLE
        }
    }
}

class CatAdapter(private val itemClick: (Category?) -> Unit) : RecyclerView.Adapter<CatAdapter.ViewHolder>() {

    var catList = mutableListOf<Category>()
    var chosenList = mutableListOf<Category>()

    fun clearAndAddList(list: List<Category>, cList: List<Category>) {
        catList.clear()
        catList.addAll(list)
        chosenList.clear()
        chosenList.addAll(cList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_category_filter, parent, false
        ), itemClick
    )

    private fun getItem(position: Int) = catList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), chosenList.find { it.id == getItem(position).id } != null)

    override fun getItemCount() = catList.size

    inner class ViewHolder(itemView: View, private val listener: (Category?) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Category?, chosen: Boolean) {
            itemView.name.text = cat?.pagetitle
            itemView.check.isChecked = chosen
            itemView.setOnClickListener { listener.invoke(cat) }
        }
    }
}

class RetailAdapter(private val itemClick: (Retail?) -> Unit) : RecyclerView.Adapter<RetailAdapter.ViewHolder>() {

    var retList = mutableListOf<Retail>()
    var chosenList = mutableListOf<Retail>()

    fun clearAndAddList(list: List<Retail>, cList: List<Retail>) {
        retList.clear()
        retList.addAll(list)
        chosenList.clear()
        chosenList.addAll(cList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.view_retail_filter, parent, false
        ), itemClick
    )

    private fun getItem(position: Int) = retList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position), chosenList.find { it.id == getItem(position).id } != null)

    override fun getItemCount() = retList.size

    inner class ViewHolder(itemView: View, private val listener: (Retail?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Retail?, chosen: Boolean) {
            GlideApp.with(itemView.context).load(cat?.logo).into(itemView.img)
            itemView.title.text = cat?.pagetitle
            itemView.checker.isChecked = chosen
            itemView.setOnClickListener {
                listener.invoke(cat)
            }
        }
    }
}
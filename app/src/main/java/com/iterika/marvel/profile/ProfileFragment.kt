package com.iterika.marvel.profile

import android.arch.lifecycle.Observer
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.Prefs
import com.iterika.marvel.R
import com.iterika.marvel.auth.AuthViewModel
import com.iterika.marvel.auth.TotalSaved
import com.iterika.marvel.coupons.CouponsRepo
import com.iterika.marvel.navigation.AUTH
import com.iterika.marvel.navigation.TAB_PROFILE
import com.iterika.marvel.utils.hideKbd
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    val navigator: Cicerone<Router> by inject()
    val viewModel: AuthViewModel by viewModel()
    val prefs: Prefs by inject()
    val couponsRepo: CouponsRepo by inject()

    val observer: Observer<TotalSaved> by lazy {
        Observer<TotalSaved> {
            refresh.isRefreshing = false
            if (it?.saved == null || it.saved.isEmpty()) {
                //message(it?.err!!)
                sum.text = "0\u20BD"
            } else {
                sum.text = "${it.saved}\u20BD"
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.hideKbd()

        if (prefs.token.isEmpty()) {
            profile_no.visibility = View.VISIBLE
            profile_yes.visibility = View.GONE
            logout.visibility = View.GONE
            login.visibility = View.VISIBLE
        } else {
            logout.visibility = View.VISIBLE
            login.visibility = View.GONE
            profile_no.visibility = View.GONE
            profile_yes.visibility = View.VISIBLE
            viewModel.totalSavedData.observe(this, observer)
            email.text = prefs.email
            refresh.isRefreshing = true
            viewModel.totalSaved()
            refresh.setOnRefreshListener {
                viewModel.totalSaved()
            }
        }

        privacy.setOnClickListener {
            goLink("https://skidkimne.ru/assets/files/conf.pdf")
        }

        conditions.setOnClickListener {
            goLink("https://skidkimne.ru/assets/files/soglashenie.pdf")
        }

        howWorking.setOnClickListener {
            goLink("https://skidkimne.ru/how_it_works.html")
        }

        logout.setOnClickListener {
            prefs.token = ""
            prefs.email = ""
            couponsRepo.clearCache()
            navigator.router.newRootScreen(TAB_PROFILE)
        }

        login.setOnClickListener {
            navigator.router.navigateTo(AUTH)
        }
    }

    fun goLink(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        try { startActivity(i) } catch (e: ActivityNotFoundException) { e.printStackTrace() }
    }
}
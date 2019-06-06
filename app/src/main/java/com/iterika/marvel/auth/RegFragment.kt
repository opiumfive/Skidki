package com.iterika.marvel.auth

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.Prefs
import com.iterika.marvel.R
import com.iterika.marvel.navigation.CART
import com.iterika.marvel.navigation.TAB_PROFILE
import com.iterika.marvel.utils.message
import kotlinx.android.synthetic.main.fragment_reg.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class RegFragment : Fragment() {

    val viewModel: AuthViewModel by viewModel()
    val navigator: Cicerone<Router> by inject()
    val prefs: Prefs by inject()

    val observer: Observer<AuthResult> by lazy {
        Observer<AuthResult> {
            if (it?.token == null || it.token.isEmpty()) {
                emailText.error = it?.err ?: "Ошибка"
                passText.error = it?.err ?: "Ошибка"
            } else {
                prefs.token = it.token
                prefs.email = email.text.toString()
                if (toCart == true) {
                    navigator.router.newRootScreen(CART, toCart)
                } else {
                    navigator.router.newRootScreen(TAB_PROFILE)
                }
            }
        }
    }

    var toCart: Boolean? = false

    companion object {
        private val ARG_CART = "cart"

        fun newInstance(toCart: Boolean = false): RegFragment {
            val fragment = RegFragment()
            val args = Bundle()
            args.putBoolean(ARG_CART, toCart)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toCart = arguments?.getBoolean(ARG_CART)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authData.observe(this, observer)

        reg.setOnClickListener {
            if (validate()) {
                viewModel.register(email.text.toString(), pass.text.toString())
            } else {
                message("Нужно заполнить все поля")
            }
        }

        back.setOnClickListener { navigator.router.exit() }
    }

    fun validate() = email.text.toString().isNotEmpty()
            && pass.text.toString().isNotEmpty()
            && rpass.text.toString() == pass.text.toString()
}
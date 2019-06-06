package com.iterika.marvel.auth

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iterika.marvel.R
import com.iterika.marvel.navigation.TAB_PROFILE
import com.iterika.marvel.utils.message
import kotlinx.android.synthetic.main.fragment_restore.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

class RestoreFragment : Fragment() {

    val viewModel: AuthViewModel by viewModel()
    val navigator: Cicerone<Router> by inject()

    val observer: Observer<AuthResult> by lazy {
        Observer<AuthResult> {

            if (it?.message != null && it.message.isNotEmpty()) {
                message(it.message)
            } else if (it?.err != null && it.err.isNotEmpty()) {
                message(it.err)
            } else {
                message("Ошибка")
            }

            navigator.router.navigateTo(TAB_PROFILE)
        }
    }

    companion object {
        fun newInstance() = RestoreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authData.observe(this, observer)

        restore.setOnClickListener {
            if (validate()) viewModel.restore(user.text.toString())
        }

        back.setOnClickListener { navigator.router.exit() }
    }

    fun validate() = user.text.toString().isNotEmpty()
}
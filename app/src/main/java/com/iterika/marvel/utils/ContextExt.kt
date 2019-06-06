package com.iterika.marvel.utils

import android.content.Context
import android.os.Handler
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import android.app.Activity
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat.getSystemService




val Context.screenWidthPx
    get() = resources.displayMetrics.widthPixels

fun Context.dp2px(value: Int): Int = (value * this.resources.displayMetrics.density).toInt()

fun Context.drawable(@DrawableRes res: Int) = ContextCompat.getDrawable(this, res)

fun Context.showKbd() {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun FragmentActivity.hideKbd() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun EditText.onTextChangedDebounced(debounce: Long = 500, onTextChanged: (String) -> Unit) {

    val handler = Handler()
    var currentText = ""
    val runnable = { onTextChanged.invoke(currentText) }

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            currentText = p0.toString()
            if (currentText.isNotEmpty()) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(runnable, debounce)
            }
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

fun Fragment.message(mess: String) = this.activity?.alert(mess) { okButton { /*stub*/ } }?.show()
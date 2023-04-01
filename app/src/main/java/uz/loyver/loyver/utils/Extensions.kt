package uz.loyver.loyver.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal


fun Fragment.toast(msg: String) {
    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    Log.d("FROM TOAST", "MESSAGE: --->   $msg")
}

fun toast(view: View, msg: String) {
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    Log.d("FROM TOAST", "MESSAGE: --->   $msg")
}

fun Fragment.hideKeyboard(){
    val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = activity!!.currentFocus
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0);
}

fun String.setAsPrice(): String {
    var result = ""
    for (i in 1..this.length) {
        val ch: Char = this[this.length - i]
        if (i % 3 == 1 && i > 1) {
            result = ",$result"
        }
        result = ch.toString() + result
    }

    return result
}

fun getAsPrice(str: String): String {
    var result = ""
    for (i in 1..str.length) {
        val ch: Char = str[str.length - i]
        if (i % 3 == 1 && i > 1) {
            result = ",$result"
        }
        result = ch.toString() + result
    }

    return result
}

fun getAsPrice(number: Double): String {
    val str = number.toInt().toString()
    var result = ""
    for (i in 1..str.length) {
        val ch: Char = str[str.length - i]
        if (i % 3 == 1 && i > 1) {
            result = ",$result"
        }
        result = ch.toString() + result
    }

    return result
}

fun Float.toStringAsPrice(): String? {
    return if (this.isNaN() || this.isInfinite()) this.toString() else BigDecimal(this.toString()).stripTrailingZeros()
        .toPlainString()
}
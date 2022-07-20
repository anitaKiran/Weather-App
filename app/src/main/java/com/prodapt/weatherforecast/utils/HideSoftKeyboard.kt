package com.prodapt.weatherforecast

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Anita Kiran on 7/20/2022.
 */
object HideSoftKeyboard {

    fun hide(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.example.clonetelegram.UI.fragments

import androidx.fragment.app.Fragment
import com.example.clonetelegram.activity.MainActivity
import com.example.clonetelegram.utils.APP_ACTIVITY
import com.example.clonetelegram.utils.hideKeyboard


open class BaseFragment(layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.mAppDrawer.enableDrawer()

    }
}
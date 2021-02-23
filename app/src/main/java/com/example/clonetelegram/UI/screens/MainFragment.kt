package com.example.clonetelegram.UI.screens


import androidx.fragment.app.Fragment
import com.example.clonetelegram.R
import com.example.clonetelegram.utils.APP_ACTIVITY
import com.example.clonetelegram.utils.hideKeyboard


class MainFragment : Fragment(R.layout.fragment_chat) {



    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Chats"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}
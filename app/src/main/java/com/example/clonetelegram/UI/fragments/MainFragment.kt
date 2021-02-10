package com.example.clonetelegram.UI.fragments


import androidx.fragment.app.Fragment
import com.example.clonetelegram.R
import com.example.clonetelegram.utils.APP_ACTIVITY


class MainFragment : Fragment(R.layout.fragment_chat) {



    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Chats"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
    }
}
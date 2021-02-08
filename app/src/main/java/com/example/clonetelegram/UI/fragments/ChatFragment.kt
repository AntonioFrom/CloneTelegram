package com.example.clonetelegram.UI.fragments


import androidx.fragment.app.Fragment
import com.example.clonetelegram.R
import com.example.clonetelegram.utils.APP_ACTIVITY


class ChatFragment : Fragment(R.layout.fragment_chat) {



    override fun onResume() {
        APP_ACTIVITY.title = "Chats"
        super.onResume()
    }
}
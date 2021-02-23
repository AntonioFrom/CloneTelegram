package com.example.clonetelegram.UI.message_recycler_view.view_holders

import com.example.clonetelegram.UI.message_recycler_view.view.MessageView

interface MessageHolders {

    fun drawMessage (view: MessageView)
    fun onAttach(view: MessageView)
    fun onDetach()
}
package com.example.clonetelegram.UI.fragments.message_recycler_view.view

import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.utils.TYPE_MESSAGE_IMAGE
import com.example.clonetelegram.utils.TYPE_MESSAGE_VOICE

class AppViewFactory {
    companion object {
        fun getView(message: CommonModel): MessageView {
            return when (message.type) {
                TYPE_MESSAGE_IMAGE -> ViewImageMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.textMessage
                )
                TYPE_MESSAGE_VOICE -> ViewVoiceMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.textMessage
                )
                else -> ViewTextMessage(
                    message.id,
                    message.from,
                    message.timeStamp.toString(),
                    message.fileUrl,
                    message.textMessage
                )
            }
        }
    }
}
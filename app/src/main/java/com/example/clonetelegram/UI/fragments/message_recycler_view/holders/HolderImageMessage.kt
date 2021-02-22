package com.example.clonetelegram.UI.fragments.message_recycler_view.holders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.UI.fragments.message_recycler_view.view.MessageView
import com.example.clonetelegram.database.CURRNET_UID
import com.example.clonetelegram.utils.asTime
import com.example.clonetelegram.utils.downloadAndSetImage
import kotlinx.android.synthetic.main.message_image_item.view.*

class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view) {

    val blocReceivedImageMessage: ConstraintLayout = view.bloc_received_image_message
    val blocUserImageMessage: ConstraintLayout = view.bloc_user_image_message
    val chatUserImage: ImageView = view.chat_user_image
    val chatReceivedImage: ImageView = view.chat_received_image
    val chatUserImageMessageTime: TextView = view.chat_user_image_message_time
    val chatReceivedImageMessageTime: TextView = view.chat_received_image_message_time

    fun drawMessageImage(holder: HolderImageMessage, view: MessageView) {
        if (view.from == CURRNET_UID) {
            holder.blocReceivedImageMessage.visibility = View.GONE
            holder.blocUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(view.fileUrl)

            holder.chatUserImageMessageTime.text =
                view.timeStamp.asTime()
        } else {
            holder.blocReceivedImageMessage.visibility = View.VISIBLE
            holder.blocUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(view.fileUrl)
            holder.chatReceivedImageMessageTime.text =
                view.timeStamp.asTime()
        }
    }
}
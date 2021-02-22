package com.example.clonetelegram.UI.fragments.message_recycler_view.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.R
import com.example.clonetelegram.UI.fragments.message_recycler_view.view.MessageView
import com.example.clonetelegram.utils.TYPE_MESSAGE_IMAGE

class AppHolderFactory {

    companion object{
        fun getHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder{
            return when(viewType){
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_image_item, parent, false)
                    HolderImageMessage(view)
                } else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_text_item, parent, false)
                    HolderTextMessage(view)
                }
            }
        }
    }
}
package com.example.clonetelegram.UI.fragments.SingleChat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.R
import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.database.CURRNET_UID
import com.example.clonetelegram.utils.asTime
import kotlinx.android.synthetic.main.message_item.view.*

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    var mlistMessageCache = emptyList<CommonModel>()

    class SingleChatHolder(view: View):RecyclerView.ViewHolder(view){
        val blocUserMessage:ConstraintLayout = view.bloc_user_message
        val chatUserMessage:TextView = view.chat_user_message
        val chatUserMessageTime:TextView = view.chat_user_message_time

        val blocReceivedMessage:ConstraintLayout = view.bloc_received_message
        val chatReceivedMessage:TextView = view.chat_received_message
        val chatReceivedMessageTime:TextView = view.chat_received_message_time
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return SingleChatHolder(view)
    }

    override fun getItemCount(): Int = mlistMessageCache.size

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        if (mlistMessageCache[position].from == CURRNET_UID){
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mlistMessageCache[position].textMessage
            holder.chatUserMessageTime.text = mlistMessageCache[position].timeStamp.toString().asTime()
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mlistMessageCache[position].textMessage
            holder.chatReceivedMessageTime.text = mlistMessageCache[position].timeStamp.toString().asTime()
        }
    }
    fun setList(list: List<CommonModel>) {
        mlistMessageCache = list
        notifyDataSetChanged()
    }
}



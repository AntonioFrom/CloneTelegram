package com.example.clonetelegram.UI.fragments.SingleChat

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.AppHolderFactory
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.HolderImageMessage
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.HolderTextMessage
import com.example.clonetelegram.UI.fragments.message_recycler_view.view.MessageView
import com.example.clonetelegram.database.CURRNET_UID
import com.example.clonetelegram.utils.asTime
import com.example.clonetelegram.utils.downloadAndSetImage


class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mlistMessageCache = mutableListOf<MessageView>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemCount(): Int = mlistMessageCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder){
            is HolderImageMessage -> drawMessageImage(holder,position)
            is HolderTextMessage -> drawMessageText(holder,position)
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mlistMessageCache[position].getTypeView()
    }

    private fun drawMessageImage(holder: HolderImageMessage, position: Int) {
        if (mlistMessageCache[position].from == CURRNET_UID) {
            holder.blocReceivedImageMessage.visibility = View.GONE
            holder.blocUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mlistMessageCache[position].fileUrl)
            Log.e("muTag", mlistMessageCache[position].fileUrl)
            holder.chatUserImageMessageTime.text =
                mlistMessageCache[position].timeStamp.asTime()
        } else {
            holder.blocReceivedImageMessage.visibility = View.VISIBLE
            holder.blocUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mlistMessageCache[position].fileUrl)
            holder.chatReceivedImageMessageTime.text =
                mlistMessageCache[position].timeStamp.asTime()
        }
    }

    private fun drawMessageText(holder: HolderTextMessage, position: Int) {
        if (mlistMessageCache[position].from == CURRNET_UID) {
            holder.blocUserMessage.visibility = View.VISIBLE
            holder.blocReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mlistMessageCache[position].text
            Log.e("muTag", mlistMessageCache[position].text)
            holder.chatUserMessageTime.text =
                mlistMessageCache[position].timeStamp.asTime()
        } else {
            holder.blocUserMessage.visibility = View.GONE
            holder.blocReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mlistMessageCache[position].text
            holder.chatReceivedMessageTime.text =
                mlistMessageCache[position].timeStamp.asTime()
        }
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mlistMessageCache.contains(item)) {
            mlistMessageCache.add(item)
            notifyItemInserted(mlistMessageCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mlistMessageCache.contains(item)) {
            mlistMessageCache.add(item)
            mlistMessageCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
//    fun addItem(item: CommonModel,toBottom:Boolean,onSuccess: () -> Unit ) {
//        if (toBottom){
//            if (!mlistMessageCache.contains(item)){
//                mlistMessageCache.add(item)
//                notifyItemInserted(mlistMessageCache.size)
//            }
//        } else{
//            if (!mlistMessageCache.contains(item)){
//                mlistMessageCache.add(item)
//                mlistMessageCache.sortBy { it.timeStamp.toString() }
//                notifyItemInserted(0)
//            }
//            onSuccess()
//        }

//        val newList = mutableListOf<CommonModel>()
//        newList.addAll(mlistMessageCache)
//        if (!newList.contains(item)) newList.add(item)
//        newList.sortBy { it.timeStamp.toString() }
//        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(mlistMessageCache, newList))
//        mDiffResult.dispatchUpdatesTo(this)
//        mlistMessageCache = newList
    //   }
}



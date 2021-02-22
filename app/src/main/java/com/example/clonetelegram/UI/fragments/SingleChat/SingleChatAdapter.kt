package com.example.clonetelegram.UI.fragments.SingleChat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.AppHolderFactory
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.HolderImageMessage
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.HolderTextMessage
import com.example.clonetelegram.UI.fragments.message_recycler_view.holders.HolderVoiceMessage
import com.example.clonetelegram.UI.fragments.message_recycler_view.view.MessageView


class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mListMessageCache = mutableListOf<MessageView>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemCount(): Int = mListMessageCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HolderImageMessage -> holder.drawMessageImage(holder, mListMessageCache[position])
            is HolderTextMessage -> holder.drawMessageText(holder, mListMessageCache[position])
            is HolderVoiceMessage -> holder.drawMessageVoice(holder, mListMessageCache[position])
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessageCache[position].getTypeView()
    }

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessageCache.contains(item)) {
            mListMessageCache.add(item)
            notifyItemInserted(mListMessageCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mListMessageCache.contains(item)) {
            mListMessageCache.add(item)
            mListMessageCache.sortBy { it.timeStamp.toString() }
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



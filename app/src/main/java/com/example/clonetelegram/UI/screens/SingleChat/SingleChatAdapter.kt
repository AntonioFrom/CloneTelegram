package com.example.clonetelegram.UI.screens.SingleChat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.UI.message_recycler_view.view.MessageView
import com.example.clonetelegram.UI.message_recycler_view.view_holders.*


class SingleChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mListMessageCache = mutableListOf<MessageView>()
    private var mListHolders = mutableListOf<MessageHolders>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun getItemCount(): Int = mListMessageCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolders).drawMessage(mListMessageCache[position])
    }

    override fun getItemViewType(position: Int): Int {
        return mListMessageCache[position].getTypeView()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolders).onAttach(mListMessageCache[holder.adapterPosition])
        mListHolders.add((holder as MessageHolders))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolders).onDetach()
        mListHolders.remove((holder as MessageHolders))
        super.onViewDetachedFromWindow(holder)
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

    fun onDestroy() {
        mListHolders.forEach {
            it.onDetach()
        }
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



package com.example.clonetelegram.UI.fragments.SingleChat

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clonetelegram.R
import com.example.clonetelegram.UI.fragments.BaseFragment
import com.example.clonetelegram.database.*
import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.models.UserModel
import com.example.clonetelegram.utils.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 10
    private var mIsScroling = false
    private var mSmoothScrollToPosttion = true
//    private var mListListeners = mutableListOf<AppChildEventListener>()
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout


    override fun onResume() {
        super.onResume()
        mSwipeRefreshLayout = chat_swipe_refresh
        initToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycler_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(
            NODE_MESSAGES
        )
            .child(CURRNET_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppChildEventListener {
            mAdapter.addItem(it.getCommonModel(), mSmoothScrollToPosttion){
                if (mSmoothScrollToPosttion) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            }
            mSwipeRefreshLayout.isRefreshing = false
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        //       mListListeners.add(mMessagesListener)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScroling && dy < 0) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScroling = true
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosttion = false
        mIsScroling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
        //      mListListeners.add(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUser = REF_DATABASE_ROOT.child(
            NODE_USERS
        ).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            mSmoothScrollToPosttion = true
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("введите сообзщение")
            } else sendMessage(
                message,
                contact.id,
                TYPE_TEXT
            ) {
                chat_input_message.setText("")
            }
        }
    }


    private fun initInfoToolbar() {
        if (mReceivingUser.fullname.isEmpty()) {
            mToolbarInfo.toolbar_chat_fullname.text = contact.fullname
        } else mToolbarInfo.toolbar_chat_fullname.text = mReceivingUser.fullname

        mToolbarInfo.toolbar_chat_image.downloadAndSetImage(mReceivingUser.photoURL)
        mToolbarInfo.toolbar_chat_status.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
//        mListListeners.forEach {
//            mRefMessages.removeEventListener(it)
//        }
    }
}
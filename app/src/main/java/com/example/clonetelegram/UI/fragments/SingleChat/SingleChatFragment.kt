package com.example.clonetelegram.UI.fragments.SingleChat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clonetelegram.R
import com.example.clonetelegram.UI.fragments.BaseFragment
import com.example.clonetelegram.database.*
import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.models.UserModel
import com.example.clonetelegram.utils.*
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    //  private var mListListeners = mutableListOf<AppChildEventListener>()
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
//    private var messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES)
//        .child(CURRNET_UID).child(contact.id).push().key.toString()
//    private var path = REF_STORAGE_ROOT
//        .child(FOLDER_MESSAGES_IMAGES)
//        .child(messageKey)


    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
//        contact.imageUrl = path.toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        mSwipeRefreshLayout = chat_swipe_refresh
        mLayoutManager = LinearLayoutManager(this.context)
        chat_input_message.addTextChangedListener(AppTextWatcher {
            val string = chat_input_message.text.toString()
            if (string.isEmpty() || string == "Record") {
                chat_btn_send_message.visibility = View.GONE
                chat_btn_attach.visibility = View.VISIBLE
                chat_btn_voice.visibility = View.VISIBLE
            } else {
                chat_btn_voice.visibility = View.GONE
                chat_btn_send_message.visibility = View.VISIBLE
                chat_btn_attach.visibility = View.GONE
            }
        })
        chat_btn_attach.setOnClickListener { attachFile() }

        CoroutineScope(Dispatchers.IO).launch {

            chat_btn_voice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        chat_input_message.setText("Record")
                        chat_btn_voice.setColorFilter(ContextCompat.getColor(APP_ACTIVITY,R.color.material_drawer_selected_text))
                    } else if (event.action == MotionEvent.ACTION_UP) {

                        chat_input_message.setText("")
                        chat_btn_voice.setColorFilter(null)
                    }
                }

                true
            }
        }
    }

    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
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
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mMessagesListener = AppChildEventListener {
            val message = it.getCommonModel()
            if (mSmoothScrollToPosttion) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }

//            mAdapter.addItem(it.getCommonModel(), mSmoothScrollToPosttion){
//                if (mSmoothScrollToPosttion) {
//                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
//                }
//                mSwipeRefreshLayout.isRefreshing = false
//            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
//      mListListeners.add(mMessagesListener)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScroling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES)
                .child(CURRNET_UID).child(contact.id).push().key.toString()
            val path = REF_STORAGE_ROOT
                .child(FOLDER_MESSAGES_IMAGES)
                .child(messageKey)


            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
//                    contact.imageUrl = path.toString()
                    sendMessageAsImage(contact.id, imageUrl = it, messageKey = messageKey)
                    mSmoothScrollToPosttion = true
                }
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
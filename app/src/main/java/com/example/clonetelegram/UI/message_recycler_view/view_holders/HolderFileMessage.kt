package com.example.clonetelegram.UI.message_recycler_view.view_holders

import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.clonetelegram.UI.message_recycler_view.view.MessageView
import com.example.clonetelegram.database.CURRNET_UID
import com.example.clonetelegram.database.getFileFromStorage
import com.example.clonetelegram.utils.WRITE_FILES
import com.example.clonetelegram.utils.asTime
import com.example.clonetelegram.utils.checkPermission
import com.example.clonetelegram.utils.showToast
import kotlinx.android.synthetic.main.message_file_item.view.*
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolders {


    private val blocReceivedFileMessage: ConstraintLayout = view.bloc_received_file_message
    private val blocUserFileMessage: ConstraintLayout = view.bloc_user_file_message
    private val chatUserFileMessageTime: TextView = view.chat_user_file_message_time
    private val chatReceivedFileMessageTime: TextView = view.chat_received_file_message_time
    private val chatUserFileName: TextView = view.chat_user_file_name
    private val chatUserBtnDownload: ImageView = view.chat_user_btn_download
    private val chatUserProgressBar: ProgressBar = view.chat_user_progressbar
    private val chatReceivedFileName: TextView = view.chat_received_file_name
    private val chatReceivedBtnDownload: ImageView = view.chat_received_btn_download
    private val chatReceivedProgressBar: ProgressBar = view.chat_received_progressbar

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRNET_UID) {
            blocReceivedFileMessage.visibility = View.GONE
            blocUserFileMessage.visibility = View.VISIBLE
            chatUserFileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blocReceivedFileMessage.visibility = View.VISIBLE
            blocUserFileMessage.visibility = View.GONE
            chatReceivedFileMessageTime.text = view.timeStamp.asTime()
            chatReceivedFileName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRNET_UID) {
            chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
        } else {
            chatReceivedBtnDownload.setOnClickListener { clickToBtnFile(view) }
        }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRNET_UID) {
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )
        try {
            if (checkPermission(WRITE_FILES)) {
                file.createNewFile()
 //               Log.e("TAG", file.toString() + view.fileUrl)
                getFileFromStorage(file, view.fileUrl) {
                    if (view.from == CURRNET_UID) {
                        chatUserBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatReceivedBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDetach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}
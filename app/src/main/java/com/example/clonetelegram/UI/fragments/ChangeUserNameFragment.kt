package com.example.clonetelegram.UI.fragments

import com.example.clonetelegram.R
import com.example.clonetelegram.utils.*
import kotlinx.android.synthetic.main.fragment_change_user_name.*
import java.util.*


class ChangeUserNameFragment : BaseChangeFragment(R.layout.fragment_change_user_name) {

    lateinit var mNewUserName: String

    override fun onResume() {
        super.onResume()

        settings_input_username.setText(USER.username)
    }

    override fun change() {
        mNewUserName = settings_input_username.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUserName.isEmpty()) {
            showToast("поле пустое")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener {
                    if (it.hasChild(mNewUserName)) {
                        showToast("Такой пользователь уже есть")
                    } else {
                        changeUserName()
                    }
                })

        }
    }

    private fun changeUserName() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUserName).setValue(CURRNET_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUserName()
                }
            }
    }

    private fun updateCurrentUserName() {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRNET_UID).child(CHILD_USER_NAME)
            .setValue(mNewUserName)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("update is successful")
                    deleteOldUserName()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUserName() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("update is successful")
                    fragmentManager?.popBackStack()
                    USER.username = mNewUserName
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }
}
package com.example.clonetelegram.UI.screens.settings

import com.example.clonetelegram.R
import com.example.clonetelegram.UI.screens.BaseChangeFragment
import com.example.clonetelegram.database.*
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
            REF_DATABASE_ROOT.child(
                NODE_USER_NAMES
            )
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
        REF_DATABASE_ROOT.child(
            NODE_USER_NAMES
        ).child(mNewUserName).setValue(CURRNET_UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUserName(mNewUserName)
                }
            }
    }


}
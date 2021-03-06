package com.example.clonetelegram.UI.screens.register

import androidx.fragment.app.Fragment
import com.example.clonetelegram.R
import com.example.clonetelegram.database.*
import com.example.clonetelegram.utils.*
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*


class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {


    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.title = phoneNumber
        register_input_code.addTextChangedListener(AppTextWatcher {

            val string = register_input_code.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    fun enterCode() {
        val code = register_input_code.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber

                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener {
                        if (!it.hasChild(CHILD_USER_NAME)) {
                            dateMap[CHILD_USER_NAME] = uid
                            REF_DATABASE_ROOT.child(
                                NODE_PHONES
                            ).child(phoneNumber).setValue(uid)
                                .addOnFailureListener { showToast("error") }
                                .addOnSuccessListener {
                                    REF_DATABASE_ROOT.child(
                                        NODE_USERS
                                    ).child(uid).updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            restartActivity()
                                        }
                                        .addOnFailureListener {
                                            showToast(it.message.toString())
                                        }
                                }
                        }
                    })
            } else {
                showToast(task.exception?.message.toString())
            }
        }
    }

}
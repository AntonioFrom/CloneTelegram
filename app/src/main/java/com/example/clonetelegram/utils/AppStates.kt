package com.example.clonetelegram.utils

import com.example.clonetelegram.database.*

enum class AppStates(val state: String) {

    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object {
        fun updateState(appState: AppStates) {
            if (AUTH.currentUser!=null){

                REF_DATABASE_ROOT.child(
                    NODE_USERS
                ).child(CURRNET_UID).child(
                    CHILD_STATE
                )
                    .setValue(appState.state)
                    .addOnSuccessListener { USER.state = appState.state }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}
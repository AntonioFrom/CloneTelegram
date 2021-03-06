package com.example.clonetelegram.database

import android.net.Uri
import android.util.Log
import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.models.UserModel
import com.example.clonetelegram.utils.APP_ACTIVITY
import com.example.clonetelegram.utils.AppValueEventListener
import com.example.clonetelegram.utils.TYPE_MESSAGE_TEXT
import com.example.clonetelegram.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.ArrayList

fun initFirebase() {
    AUTH =
        FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER =
        UserModel()
    CURRNET_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(
        CURRNET_UID
    )
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
//    Log.e("TAG",mFile.toString() + fileUrl.toString())
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun uploadFileTOStorage(uri: Uri, messageKey: String, receivedID: String, typeMessage: String, filename: String = "") {
    val path = REF_STORAGE_ROOT.child(
        FOLDER_FILES
    ).child(messageKey)
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                fileUrl = it,
                messageKey = messageKey,
                typeMessage = typeMessage,
                filename = filename
            )
        }
    }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child((CURRNET_UID))
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER =
                it.getValue(UserModel::class.java)
                    ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username =
                    CURRNET_UID
            }
            function()
        })
}

fun saveToMainList(id: String, typeChat: String) {
        var refUser = "$NODE_MAIN_LIST/$CURRNET_UID/$id"
        var refReceived = "$NODE_MAIN_LIST/$id/$CURRNET_UID"

        val mapUser = hashMapOf<String,Any>()
        val mapReceived = hashMapOf<String,Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = typeChat
    
    mapReceived[CHILD_ID] = CURRNET_UID
    mapReceived[CHILD_TYPE] = typeChat

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener{ showToast(it.message.toString())}
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(
            NODE_PHONES
        ).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { shapshot ->
                arrayContacts.forEach { contact ->
                    if (shapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(
                            NODE_PHONES_CONTACTS
                        ).child(CURRNET_UID)
                            .child(shapshot.value.toString())
                            .child(CHILD_ID)
                            .setValue(shapshot.value.toString())
                            .addOnFailureListener {
                                showToast(
                                    it.message.toString()
                                )
                            }
                        REF_DATABASE_ROOT.child(
                            NODE_PHONES_CONTACTS
                        ).child(CURRNET_UID)
                            .child(shapshot.value.toString())
                            .child(CHILD_FULL_NAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener {
                                showToast(
                                    it.message.toString()
                                )
                            }
                    }
                }
            }
        })
    }
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()

fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {

    val refDialogUser = "$NODE_MESSAGES/$CURRNET_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRNET_UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] =
        CURRNET_UID
    mapMessage[CHILD_TYPE] =
        TYPE_MESSAGE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] =
        ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {
    val refDialogUser = "$NODE_MESSAGES/$CURRNET_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRNET_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRNET_UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_ULR] = fileUrl
    mapMessage[CHILD_TEXT] = filename


    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun updateCurrentUserName(newUserName: String) {
    REF_DATABASE_ROOT.child(
        NODE_USERS
    ).child(CURRNET_UID).child(
        CHILD_USER_NAME
    )
        .setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast("update is successful")
                deleteOldUserName(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

fun deleteOldUserName(newUserName: String) {
    REF_DATABASE_ROOT.child(
        NODE_USER_NAMES
    ).child(USER.username).removeValue()
        .addOnSuccessListener {
            showToast("update is successful")
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(
        NODE_USERS
    ).child(CURRNET_UID).child(
        CHILD_BIO
    ).setValue(newBio)
        .addOnSuccessListener {
            showToast("data update")
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun setNameToDatabase(fullname: String) {
    REF_DATABASE_ROOT.child(
        NODE_USERS
    ).child(CURRNET_UID).child(
        CHILD_FULL_NAME
    )
        .setValue(fullname).addOnSuccessListener {
            showToast("Данные обновлены")
            USER.fullname = fullname
            APP_ACTIVITY.mAppDrawer.updateHeader()
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener { showToast(it.message.toString()) }
}

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(
    NODE_MESSAGES
)
    .child(CURRNET_UID).child(id).push().key.toString()




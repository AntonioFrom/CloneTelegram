package com.example.clonetelegram.database

import android.net.Uri
import android.widget.Toast
import com.example.clonetelegram.models.CommonModel
import com.example.clonetelegram.models.UserModel
import com.example.clonetelegram.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

lateinit var AUTH: FirebaseAuth
lateinit var CURRNET_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER: UserModel
lateinit var REF_STORAGE_ROOT: StorageReference

const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_USER_NAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val FOLDER_MESSAGES_IMAGES = "message_image"

const val FOLDER_PROFILE_IMAGE = "profile_image"
const val TYPE_TEXT = "text"

const val CHILD_TEXT = "textMessage"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USER_NAME = "username"
const val CHILD_FULL_NAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoURL"
const val CHILD_STATE = "state"
const val CHILD_FILE_ULR = "fileURL"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRNET_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRNET_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}
//inline fun getUrlFromDatabase(path: DatabaseReference, crossinline function: (url: String) -> Unit) {
//    path.child().
//        .addOnSuccessListener { function(it.toString()) }
//        .addOnFailureListener { showToast(it.message.toString()) }
//}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child((CURRNET_UID))
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER =
                it.getValue(UserModel::class.java) ?: UserModel()
            if (USER.username.isEmpty()) {
                USER.username =
                    CURRNET_UID
            }
            function()
        })
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
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsImage(receivingUserID: String, imageUrl: String, messageKey: String) {
    val refDialogUser = "$NODE_MESSAGES/$CURRNET_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRNET_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRNET_UID
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_IMAGE
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_ULR] = imageUrl

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

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES)
    .child(CURRNET_UID).child(id).push().key.toString()

fun uploadFileTOStorage(uri: Uri, messagekey: String) {
    showToast("Record OK")
}
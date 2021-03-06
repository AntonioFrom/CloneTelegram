package com.example.clonetelegram.database

import com.example.clonetelegram.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

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
const val FOLDER_FILES = "messages_files"
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

const val NODE_MAIN_LIST = "main_list"
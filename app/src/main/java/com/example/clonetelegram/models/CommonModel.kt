package com.example.clonetelegram.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoURL: String = "empty",

    var textMessage: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = ""
)
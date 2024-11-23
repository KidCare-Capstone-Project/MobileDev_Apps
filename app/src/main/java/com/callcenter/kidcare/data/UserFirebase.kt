package com.callcenter.kidcare.data

data class UserFirebase(
    val username: String = "",
    val email: String = "",
    val profilePic: String? = null,
    val role: String = "",
    val uuid: String = ""
)
package com.callcenter.kidcare.ui.home.childprofile.diarymenu.data

data class DailyMenu(
    val time: String,
    val title: String,
    val thumbnailUrl: String?,
    val recipeId: String,
    val childName: String
)
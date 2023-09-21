package com.bahardev.submission

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetail(
    val name: String,
    val location: String,
    val public_repos: String,
    val followers: String,
    val following: String
): Parcelable

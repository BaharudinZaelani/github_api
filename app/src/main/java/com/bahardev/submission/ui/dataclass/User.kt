package com.bahardev.submission.ui.dataclass

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val login: String,
    val name: String,
    val avatar_url: String
) : Parcelable

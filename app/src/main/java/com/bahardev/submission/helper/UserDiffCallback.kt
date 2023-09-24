package com.bahardev.submission.helper

import androidx.recyclerview.widget.DiffUtil
import com.bahardev.submission.database.UserGithub

class UserDiffCallback(
    private val oldUserList: List<UserGithub>,
    private val newUserList: List<UserGithub>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldUserList.size
    override fun getNewListSize(): Int = newUserList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition].id == newUserList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUserList[oldItemPosition]
        val newUser = newUserList[newItemPosition]
        return oldUser.username == newUser.username && oldUser.avatarUrl == newUser.avatarUrl
    }
}
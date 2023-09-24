package com.bahardev.submission.ui.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bahardev.submission.database.UserGithub
import com.bahardev.submission.repository.UserRepository

class UserGithubModel(application: Application): ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    fun insert( userGithub: UserGithub ) {
        mUserRepository.insert(userGithub)
    }
    fun delete( userGithub: UserGithub ) {
        mUserRepository.delete(userGithub)
    }

    fun deleteByUsername( username: String ) {
        mUserRepository.deleteByUsername(username)
    }
    fun getAllUser(): LiveData<List<UserGithub>> = mUserRepository.getAllUser()

    fun getByUsername(username: String): LiveData<List<UserGithub>> = mUserRepository.getByUsername(username)
}
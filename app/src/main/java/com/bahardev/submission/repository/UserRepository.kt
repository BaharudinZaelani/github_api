package com.bahardev.submission.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.bahardev.submission.database.UserGithub
import com.bahardev.submission.database.UserGithubDao
import com.bahardev.submission.database.UserGithubDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUserGithubDao: UserGithubDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserGithubDatabase.getDatabase(application)
        mUserGithubDao = db.userGithubDao()
    }

    fun getAllUser(): LiveData<List<UserGithub>> = mUserGithubDao.getAllUser()

    fun getByUsername(username: String): LiveData<List<UserGithub>> = mUserGithubDao.getByUsername(username)

    fun deleteByUsername( username: String ) {
        executorService.execute { mUserGithubDao.deleteByUsername(username) }
    }

    fun insert( userGithub: UserGithub ) {
        executorService.execute { mUserGithubDao.insert(userGithub) }
    }

    fun delete( userGithub: UserGithub ) {
        executorService.execute { mUserGithubDao.delete(userGithub) }
    }
}
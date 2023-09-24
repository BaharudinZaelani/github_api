package com.bahardev.submission.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserGithubDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userGthub: UserGithub)

    @Delete
    fun delete(userGthub: UserGithub)

    @Query("SELECT * FROM usergithub ORDER BY id ASC")
    fun getAllUser(): LiveData<List<UserGithub>>

    @Query("SELECT * FROM usergithub WHERE username LIKE :username")
    fun getByUsername(username: String): LiveData<List<UserGithub>>

    @Query("DELETE FROM usergithub WHERE username = :username")
    fun deleteByUsername(username: String)
}
package com.bahardev.submission.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserGithub::class], version = 1)
abstract class UserGithubDatabase : RoomDatabase() {
    abstract fun userGithubDao(): UserGithubDao

    companion object {
        @Volatile
        private var INSTANCE: UserGithubDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): UserGithubDatabase {
            if ( INSTANCE == null ) {
                synchronized(UserGithubDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, UserGithubDatabase::class.java, "user_favorite_database").build()
                }
            }

            return INSTANCE as UserGithubDatabase
        }
    }
}
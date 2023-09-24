package com.bahardev.submission.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bahardev.submission.R
import com.bahardev.submission.database.UserGithub
import com.bahardev.submission.ui.adapter.UserAdapter
import com.bahardev.submission.ui.dataclass.User
import com.bahardev.submission.ui.models.UserGithubModel
import com.bahardev.submission.ui.models.ViewModelFactory
import org.json.JSONArray

class FavoriteUser : AppCompatActivity() {
    private val list = mutableListOf<User>()
    private lateinit var favUserContainer: RecyclerView

    private fun construct() {
        favUserContainer = findViewById(R.id.favUserRecyclerView)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_fav_user)
        construct()

        favUserContainer.setHasFixedSize(true)

        val userModel = obtainViewModel(this@FavoriteUser)
        userModel.getAllUser().observe(this) { userList ->
            userList.map {res ->
                var result = User(res.username.toString(), res.avatarUrl.toString(), "")
                list.add(result)
            }
            showRecyclers()
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserGithubModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserGithubModel::class.java)
    }
    private fun showRecyclers() {
        favUserContainer.layoutManager = LinearLayoutManager(this)
        val listUser = UserAdapter(list)
        favUserContainer.adapter = listUser

        listUser.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val showUser = Intent(this@FavoriteUser, ShowUser::class.java)
                showUser.putExtra("photo", data.avatar_url)
                showUser.putExtra("name", data.login)
                showUser.putExtra("id", data.name)
                startActivity(showUser)
            }
        })
    }
}


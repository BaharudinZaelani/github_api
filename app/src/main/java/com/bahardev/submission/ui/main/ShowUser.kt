package com.bahardev.submission.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import com.bahardev.submission.R
import com.bahardev.submission.database.UserGithub
import com.bahardev.submission.ui.adapter.SectionsPagerAdapter
import com.bahardev.submission.databinding.ActivityShowUserBinding
import com.bahardev.submission.helper.DateHelper
import com.bahardev.submission.ui.models.UserGithubModel
import com.bahardev.submission.ui.models.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class ShowUser : AppCompatActivity() {
    private lateinit var binding: ActivityShowUserBinding
    private lateinit var userGithubModel: UserGithubModel
    private var userGithub: UserGithub = UserGithub()
    private var tmpImageAvatar: String = ""

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_1,
            R.string.tab_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch data user
        fetchData(intent.getStringExtra("name").toString())

        // Show User
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.userName = intent.getStringExtra("name").toString()
        binding.viewPager.adapter = sectionsPagerAdapter

        // Tab
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])

        }.attach()
        supportActionBar?.elevation = 0f

        // check has fav
        val userModel = obtainViewModel(this@ShowUser)
        userGithubModel = obtainViewModel(this@ShowUser)
        val favActivity = Intent(this@ShowUser, FavoriteUser::class.java)
        userModel.getByUsername(intent.getStringExtra("name").toString()).observe(this) { userList ->
            val user = JSONArray(userList)
            if ( user.length() > 0 ) {
                binding.fabAdd.setImageResource(R.drawable.clear)
                binding.fabAdd.setOnClickListener {
                    userGithub.let {
                        userGithubModel.deleteByUsername(intent.getStringExtra("name").toString())
                    }
                    binding.fabAdd.setImageResource(R.drawable.bookmark)
                    startActivity(favActivity)
                }
            }else {
                binding.fabAdd.setOnClickListener {
                    userGithub.let {
                        userGithub.username = intent.getStringExtra("name").toString()
                        userGithub.avatarUrl = tmpImageAvatar
                        userGithub.date = DateHelper.getCurrentDate()
                        userGithubModel.insert(userGithub as UserGithub)
                    }
                    startActivity(favActivity)
                }
            }
        }

    }

    private fun obtainViewModel(activity: AppCompatActivity): UserGithubModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(UserGithubModel::class.java)
    }

    private fun fetchData(key: String, addToFav: Boolean = false) {
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "Token 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")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$key", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val res = JSONObject(String(responseBody))
                    tmpImageAvatar = res.getString("avatar_url")

                    Glide.with(this@ShowUser).load(res.getString("avatar_url")).into(binding.imgUser)
                    binding.userName.text = intent.getStringExtra("name")
                    binding.userGithubName.text = res.getString("name")
                    binding.countFollowers.text = "Followers :" + res.getString("followers")
                    binding.countFollowing.text = "Following :" + res.getString("following")

                    binding.loaderProfile.visibility = View.GONE
                    binding.fabAdd.visibility = View.VISIBLE
                }catch (e: Exception) {
                    Log.d("ERROR", e.message.toString())
                    binding.loaderProfile.visibility = View.GONE
                }
            }
            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.d("ERROR", errorMessage)
                binding.loaderProfile.visibility = View.GONE
            }
        })
    }
}
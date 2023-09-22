package com.bahardev.submission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import com.bahardev.submission.databinding.ActivityShowUserBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class ShowUser : AppCompatActivity() {
    private lateinit var binding: ActivityShowUserBinding

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


        Log.d("ZAW", intent.getStringExtra("photo").toString())
        fetchData(intent.getStringExtra("name").toString())

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        sectionsPagerAdapter.userName = intent.getStringExtra("name").toString()
        binding.viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])

        }.attach()
        supportActionBar?.elevation = 0f

    }

    private fun fetchData(key: String) {
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "Token your_token")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$key", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val res = JSONObject(String(responseBody))
                    Glide.with(this@ShowUser).load(res.getString("avatar_url")).into(binding.imgUser)
                    binding.userName.text = intent.getStringExtra("name")
                    binding.userGithubName.text = res.getString("name")
                    binding.loaderProfile.visibility = View.GONE
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
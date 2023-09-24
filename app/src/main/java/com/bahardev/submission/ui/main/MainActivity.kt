package com.bahardev.submission.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bahardev.submission.R
import com.bahardev.submission.ui.dataclass.User
import com.bahardev.submission.ui.adapter.UserAdapter
import com.bahardev.submission.databinding.ActivityMainBinding
import com.bahardev.submission.ui.models.SettingModelFactory
import com.bahardev.submission.ui.models.SettingViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val list = mutableListOf<User>()
    private var fetched : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // result
        binding.containerViewSearch.setHasFixedSize(true)

        with(binding) {
            // searchbar
            searchBar.inflateMenu(R.menu.menu_form)
            searchBar.setOnMenuItemClickListener { menuItem ->
                when( menuItem.itemId ) {
                    R.id.favorite -> {
                        val followers = Intent(this@MainActivity, FavoriteUser::class.java)
                        startActivity(followers)
                    }
                    R.id.menu2 -> {
                        val setting = Intent(this@MainActivity, SettingsActivity::class.java)
                        startActivity(setting)
                    }
                }
                true
            }


            // search view
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    // Fecth Data
                    fetchData(searchView.text.toString())
                    searchView.hide()
                    false
                }
        }

        // check theme
        val pref = SettingPreferences.getInstance(application.dataStore)
        val settingViewModel = ViewModelProvider(this, SettingModelFactory(pref)).get(
            SettingViewModel::class.java
        )
        settingViewModel.getThemeSettings().observe(this) { isDarkMode: Boolean ->
            if ( isDarkMode ) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun fetchData( key: String ) {
        binding.loader.visibility = View.VISIBLE
        list.clear()

        val client = AsyncHttpClient()
        client.addHeader("Authorization", "Token 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")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/search/users?q=$key", object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val jsonObject = JSONObject(String(responseBody))
                    val itemsArray = jsonObject.getJSONArray("items")

                    for (i in 0 until itemsArray.length()) {
                        val item = itemsArray.getJSONObject(i)
                        val login = item.getString("login")
                        val avatarUrl = item.getString("avatar_url")
                        val name = item.getString("id")
                        val result = User(login, avatarUrl, name)
                        list.add(result)
                    }
                    if ( fetched ) {

                    }
                    showRecycler()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    Log.d("ERROR", e.message.toString())
                }
                binding.loader.visibility = View.GONE
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.loader.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecycler() {
        binding.containerViewSearch.layoutManager = LinearLayoutManager(this)
        val listUser = UserAdapter(list)
        binding.containerViewSearch.adapter = listUser

        listUser.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val showUser = Intent(this@MainActivity, ShowUser::class.java)
                showUser.putExtra("photo", data.avatar_url)
                showUser.putExtra("name", data.login)
                showUser.putExtra("id", data.name)
                startActivity(showUser)
            }
        })
    }
}

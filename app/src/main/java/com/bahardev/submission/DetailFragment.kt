package com.bahardev.submission

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class DetailFragment : Fragment() {
    private lateinit var container: RecyclerView
    private  lateinit var loaderFollowers: FrameLayout
    private val listUser = mutableListOf<User>()

    companion object {
        const val ARG_NAME = "username"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view.findViewById(R.id.containerFollowers)
        loaderFollowers = view.findViewById(R.id.loaderFollowers)

        val name = arguments?.getString(ARG_NAME)
        fetchData(name.toString())
    }

    private fun fetchData(key: String) {
        val client = AsyncHttpClient()

        client.addHeader("Authorization", "Token your_token")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$key/followers", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    if ( String(responseBody) == "[]" ) {
                        loaderFollowers.visibility = View.GONE
                        return
                    }
                    val itemsArray = JSONArray(String(responseBody))

                    for (i in 0 until itemsArray.length()) {
                        val jsonObject = itemsArray.getJSONObject(i)
                        val login = jsonObject.getString("login")
                        val avatarUrl = jsonObject.getString("avatar_url")
                        val name = jsonObject.getString("id")
                        val result = User(login, avatarUrl, name)
                        listUser.add(result)
                    }
                    showRecyclers()
                }catch (e: Exception) {
                    Log.d("ERROR", e.message.toString())
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
            }
        })

    }

    private fun showRecyclers() {
        container.layoutManager = LinearLayoutManager(requireActivity())
        val listUser = UserAdapter(listUser)
        container.adapter = listUser
        loaderFollowers.visibility = View.GONE

        listUser.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                val toast = Toast.makeText(requireActivity(), "This is " + data.login, Toast.LENGTH_SHORT) // in Activity
                toast.show()
            }
        })
    }

}
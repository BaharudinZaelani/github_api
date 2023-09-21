package com.bahardev.submission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var userName: String = ""

    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DetailFragment()
            1 -> fragment = FollowersFragment()
        }
        fragment?.arguments = Bundle().apply {
            putString(DetailFragment.ARG_NAME, userName)
        }
        return fragment as Fragment
    }

}
package com.komputerkit.whatsapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.komputerkit.whatsapp.fragments.CallsFragment
import com.komputerkit.whatsapp.fragments.ChatsFragment
import com.komputerkit.whatsapp.fragments.StatusFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = 3
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatsFragment()
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatsFragment()
        }
    }
}
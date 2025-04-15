package com.example.pulseup.ui.main

import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.pulseup.databinding.FragmentMainBinding
import com.example.pulseup.ui.main.profile.ProfileFragment
import com.example.pulseup.ui.main.stats.StatsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        viewPager.adapter = ViewPagerAdapter(
            fragments = listOf(
                HomeFragment(),
                StatsFragment(),
                ProfileFragment()
            ),
            fragmentActivity = requireActivity()
        )

        viewPager.offscreenPageLimit = 3

        TabLayoutMediator( tabLayout, viewPager) { tab, index ->
            tab.text = when(index) {
                0 -> "Home"
                1 -> "Stats"
                2 -> "Profile"
                else -> throw NotFoundException("Position not found")
            }
        }.attach()
    }


}
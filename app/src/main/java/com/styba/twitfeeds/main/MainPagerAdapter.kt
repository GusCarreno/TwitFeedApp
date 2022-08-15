package com.styba.twitfeeds.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.util.SparseArrayCompat
import android.view.ViewGroup
import com.styba.twitfeeds.main.fragments.settings.SettingsFragment
import com.styba.twitfeeds.main.fragments.top.TopFragment
import com.styba.twitfeeds.main.fragments.twits.TwitFragment

class MainPagerAdapter(private val fm: FragmentManager?, private val hasMenu: Boolean) : FragmentPagerAdapter(fm) {

    private var sectionsNameList: MutableList<String> = ArrayList()
    private var sectionsList: MutableList<String> = ArrayList()
    private val fragmentTags = SparseArrayCompat<String>()

    fun initSections(sectionsNameList: MutableList<String>, sectionsList: MutableList<String>) {
        this.sectionsNameList.addAll(sectionsNameList)
        this.sectionsList.addAll(sectionsList)
    }

    override fun getItem(position: Int): Fragment {
        return if (hasMenu) {
            when(position) {
                0 -> TopFragment.newInstance()
                in 1..6 -> TwitFragment.newInstance(sectionsList[position])
                else -> SettingsFragment.newInstance()
            }
        } else {
            when(position) {
                0 -> TopFragment.newInstance()
                in 1..5 -> TwitFragment.newInstance(sectionsList[position])
                else -> SettingsFragment.newInstance()
            }
        }
    }

    override fun getCount(): Int {
        return sectionsList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return sectionsNameList[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val obj = super.instantiateItem(container, position)
        if (obj is Fragment) {
            // record the fragment tag here.
            val tag = obj.tag
            fragmentTags.put(position, tag)
        }
        return obj
    }

    fun getFragment(position: Int): Fragment? {
        val tag = fragmentTags.get(position) ?: return null
        return fm?.findFragmentByTag(tag)
    }

}
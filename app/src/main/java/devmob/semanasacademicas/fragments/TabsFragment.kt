package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import devmob.semanasacademicas.R
import kotlinx.android.synthetic.main.tab_lay.*
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT as FLAG

class TabsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.tab_lay, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = TabAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    internal class TabAdapter(manager: FragmentManager): FragmentPagerAdapter(manager, FLAG){
        private val mFragmentList = mutableListOf(EventsList(0), EventsList(1))

        override fun getItem(position: Int) = mFragmentList[position]

        override fun getCount() = 2

        override fun getPageTitle(position: Int) = if(position == 0) "TODOS" else "SALVOS"
    }
}
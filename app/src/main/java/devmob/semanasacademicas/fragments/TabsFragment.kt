package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import devmob.semanasacademicas.ARG_TYPE
import devmob.semanasacademicas.EventList
import devmob.semanasacademicas.R
import kotlinx.android.synthetic.main.activity_atividades.view.*
import kotlinx.android.synthetic.main.fragment_tabs.*



class TabsFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tabs, container, false)
        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    fun setupViewPager(viewPager: ViewPager){
        val adapter = Adapter(childFragmentManager)
        val bundleAll = Bundle()
        bundleAll.putString(ARG_TYPE, EventList.all)
        val fragmentAll = FragmentTelaPrincipal()
        fragmentAll.arguments = bundleAll
        val bundleSaved = Bundle()
        bundleSaved.putString(ARG_TYPE, EventList.saved)
        val fragmentSaved = FragmentTelaPrincipal()
        fragmentSaved.arguments = bundleSaved
        adapter.addFragment(fragmentAll, "Todos")
        adapter.addFragment(fragmentSaved, "Salvos")
        viewPager.adapter = adapter
    }

    internal class Adapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = arrayListOf<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }
}
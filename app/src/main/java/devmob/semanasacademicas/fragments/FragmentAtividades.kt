package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import devmob.semanasacademicas.ARG_LISTA_ATIVIDADES
import devmob.semanasacademicas.R
import devmob.semanasacademicas.adapters.AtividadesListAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.viewModels.SelectedWeek
import kotlinx.android.synthetic.main.content_atividades.*
import kotlinx.android.synthetic.main.fragment_atividades.view.*
import java.util.*

class FragmentAtividades: androidx.fragment.app.Fragment() {

    lateinit var model: SelectedWeek
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    private fun attTabs() {
        val dias = model.filtered.keys.sorted()
        mSectionsPagerAdapter.run{
            for(dia in dias)
                if(dia !in tabItems) tabs?.addTab(tabs.newTab().setText(dia))
            tabItems = dias
            notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_atividades, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        model = ViewModelProviders.of(activity!!).get(SelectedWeek::class.java)
        model.hasChanges.observe(this, Observer<Boolean> {
            if(it == true){
                model.filterBy(model.tipo)
                attTabs()
                model.hasChanges.postValue(false)
            }
        })

        attTabs()
    }

    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        var tabItems = emptyList<String>()

        override fun getItem(p0: Int) = DayFragment.getInstance(model.filtered[tabItems[p0]]!!)

        override fun getCount() = tabItems.size

        override fun getItemPosition(obj: Any) = POSITION_NONE
    }


    class DayFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
                = inflater.inflate(R.layout.fragment_atividades, container, false).apply {
            arguments!!.getParcelableArrayList<Atividade>(ARG_LISTA_ATIVIDADES)?.apply {
                val atividadesOrdenadas = this.sortedWith(compareBy { it.inicio })

                val viewAdapter = AtividadesListAdapter(atividadesOrdenadas)
                val viewManager = androidx.recyclerview.widget.LinearLayoutManager(context)

                listaAtividades.apply {
                    setHasFixedSize(false)
                    layoutManager = viewManager
                    adapter= viewAdapter
                }
            }
        }

        companion object{
            fun getInstance(dayActivities: ArrayList<Atividade>) = DayFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_LISTA_ATIVIDADES, dayActivities)
                }
            }
        }
    }
}
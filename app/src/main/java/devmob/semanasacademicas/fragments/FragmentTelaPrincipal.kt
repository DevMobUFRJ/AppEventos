package devmob.semanasacademicas.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.adapters.ListaDeEventosAdapter
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.content_tela_principal.*
import org.jetbrains.anko.support.v4.onRefresh

class FragmentTelaPrincipal : Fragment() {

    lateinit var viewAdapter: ListaDeEventosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_principal, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)


        val viewManager = LinearLayoutManager(this.context)
        viewAdapter = ListaDeEventosAdapter()

        lista.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        swiperefresh.onRefresh {
            swiperefresh.setRefreshing(true)
            model.loadWeeks()
        }

        model.weeks.observe(this, Observer<MutableList<Evento>>{ weekList ->
            viewAdapter.eventos = weekList!!
            swiperefresh.isRefreshing = false
        })
    }
}

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
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.content_tela_principal.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentTelaPrincipal : androidx.fragment.app.Fragment() {

    lateinit var viewAdapter: ListaDeEventosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_principal, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = true //habilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = "Eventos" //seta o titulo da toolbar, mas por algum motivo nao roda quando abre o aplicativo c:

        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)
        viewAdapter = ListaDeEventosAdapter()

        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)

        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            model.loadWeeks()
        }

        lista.apply {
            layoutManager = viewManager
            adapter= viewAdapter
        }

        model.weeks.observe(this, Observer<MutableList<Evento>>{ weekList ->
            viewAdapter.eventos = weekList!!
            swiperefresh.isRefreshing = false
            model.makeQuery()
        })
    }
}

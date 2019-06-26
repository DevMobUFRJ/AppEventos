package devmob.semanasacademicas.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.adapters.ListaDeEventosAdapter
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_event_list.*

class FragmentTelaPrincipal : androidx.fragment.app.Fragment() {

    lateinit var viewAdapter: ListaDeEventosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_event_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)
        val eventList = arguments!!.getString(ARG_TYPE)

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = true //habilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = "Eventos" //seta o titulo da toolbar, mas por algum motivo nao roda quando abre o aplicativo c:

        val viewManager = LinearLayoutManager(this.context)
        viewAdapter = ListaDeEventosAdapter()

        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)

        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = true
            model.loadWeeks()
        }

        FirebaseAuth.getInstance().addAuthStateListener {
            if (eventList == EventList.saved){
                if (it.currentUser == null){
                    lista.visibility = View.GONE
                    textNotLogged.visibility = View.VISIBLE
                }
                else {
                    lista.visibility = View.VISIBLE
                    textNotLogged.visibility = View.GONE
                }
            }
        }

        lista.apply {
            layoutManager = viewManager
            adapter= viewAdapter
        }

        model.weeks.observe(this, Observer<MutableList<Evento>>{ weekList ->
            //TODO mostrar eventos favoritos do usuário
            viewAdapter.eventos = if (eventList == EventList.saved) weekList!!.filter { it.nome.contains("Computacao") } as MutableList else weekList!!
            swiperefresh.isRefreshing = false
        })
    }
}

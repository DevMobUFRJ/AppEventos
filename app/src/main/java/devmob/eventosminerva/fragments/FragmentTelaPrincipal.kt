package devmob.eventosminerva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import devmob.eventosminerva.ARG_TYPE
import devmob.eventosminerva.EventList
import devmob.eventosminerva.R
import devmob.eventosminerva.activities.TelaPrincipal
import devmob.eventosminerva.adapters.ListaDeEventosAdapter
import devmob.eventosminerva.dataclass.Evento
import devmob.eventosminerva.viewModels.User
import devmob.eventosminerva.viewModels.WeeksList
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_event_list.*

class FragmentTelaPrincipal : androidx.fragment.app.Fragment() {

    lateinit var viewAdapter: ListaDeEventosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_event_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)
        val userFavorite = ViewModelProviders.of(this.activity!!).get(User::class.java)

        val eventList = arguments!!.getString(ARG_TYPE)

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = true //habilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = "Eventos" //seta o titulo da toolbar, mas por algum motivo nao roda quando abre o aplicativo c:

        val viewManager = LinearLayoutManager(this.context)
        viewAdapter = ListaDeEventosAdapter()

        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)

        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = eventList != EventList.saved
            model.loadWeeks()
        }

        FirebaseAuth.getInstance().addAuthStateListener {
            if (eventList == EventList.saved){
                if (it.currentUser == null){
                    lista?.visibility = View.GONE
                    textNotLogged?.visibility = View.VISIBLE
                    textNotLogged?.text = "Você precisa estar logado para acessar a essa função"
                }
                else {
                    lista?.visibility = View.VISIBLE
                    textNotLogged?.run {
                        visibility = View.GONE
                        text = "Adicione uma semana aos salvos clicando no icone ao lado na semana que quiser ;)"
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_salvar_semana, 0)
                    }

                    userFavorite.changed.observe(this, Observer {
                        swiperefresh.isRefreshing = true
                        model.loadWeeks()
                    })
                }
            }
        }

        lista.apply {
            layoutManager = viewManager
            adapter= viewAdapter
        }

        model.weeks.observe(this, Observer<MutableList<Evento>>{ weekList ->
            viewAdapter.eventos =
                if (eventList != EventList.saved) weekList!!
                else (weekList!!.filter { it.id in userFavorite.favoriteEvents } as MutableList<Evento>).apply {
                        textNotLogged.visibility = if(size == 0) View.VISIBLE else View.GONE
                    }

            swiperefresh.isRefreshing = false
        })
    }
}

package devmob.eventosminerva.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.eventosminerva.*
import devmob.eventosminerva.activities.TelaPrincipal
import devmob.eventosminerva.adapters.ListaDeFavoritosAdapter
import devmob.eventosminerva.dataclass.Atividade
import devmob.eventosminerva.viewModels.User
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import kotlinx.android.synthetic.main.fragment_minha_semana.*
import kotlinx.android.synthetic.main.fragment_minha_semana.progressBar
import kotlinx.android.synthetic.main.fragment_event_list.textNotLogged as textNotLogged1

class FragmentMinhaSemana : androidx.fragment.app.Fragment() {

    lateinit var user: User
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_minha_semana, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewAdapter = ListaDeFavoritosAdapter()
        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = false //desabilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = "Minha Semana" //seta o titulo da toolbar

        textNotLogged.text = "Adicione uma atividade na \"Minha Semana\" clicando no icone ao lado em alguma atividade;)"
        textNotLogged.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.add_favorite, 0)

        listFavorite_Days.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        user = ViewModelProviders.of(this.requireActivity()).get(User::class.java)

        user.atividades.observe(this, Observer<HashMap<String, MutableList<Atividade>>>{ atividades ->

            if(atividades.size == 0) {
                textNotLogged.visibility = View.VISIBLE
            } else {
                textNotLogged.visibility = View.GONE
            }
            viewAdapter.eventosFavoritados = atividades!!
            progressBar.dismiss()
        })
    }

}

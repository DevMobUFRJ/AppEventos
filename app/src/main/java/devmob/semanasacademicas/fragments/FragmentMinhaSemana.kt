package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import devmob.semanasacademicas.R
import devmob.semanasacademicas.adapters.ListaDeFavoritosAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dismiss
import devmob.semanasacademicas.viewModels.User
import kotlinx.android.synthetic.main.fragment_minha_semana.*

class FragmentMinhaSemana : androidx.fragment.app.Fragment() {

    lateinit var user: User
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_minha_semana, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewAdapter = ListaDeFavoritosAdapter()
        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        listFavorite_Days.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        user = ViewModelProviders.of(this.requireActivity()).get(User::class.java)

        user.atividades.observe(this, Observer<HashMap<String, MutableList<Atividade>>>{ atividades ->
            viewAdapter.eventosFavoritados = atividades!!
            progressBar.dismiss()
        })
    }

}

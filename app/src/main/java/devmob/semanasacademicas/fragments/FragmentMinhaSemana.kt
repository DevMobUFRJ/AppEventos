package devmob.semanasacademicas.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.adapters.ListaDeFavoritosAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.viewModels.User
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_minha_semana.*
import java.util.ArrayList

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

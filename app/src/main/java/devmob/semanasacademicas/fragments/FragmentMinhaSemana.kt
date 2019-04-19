package devmob.semanasacademicas.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.*
import devmob.semanasacademicas.adapters.ListaDeFavoritosAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.viewModels.MinhaSemana
import kotlinx.android.synthetic.main.fragment_minha_semana.*
import java.util.ArrayList

class FragmentMinhaSemana : Fragment() {

    lateinit var model: MinhaSemana
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_minha_semana, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewAdapter = ListaDeFavoritosAdapter()
        val viewManager = LinearLayoutManager(this.context)

        listFavorite_Days.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        model = ViewModelProviders.of(this).get(MinhaSemana::class.java)
        model.mAuth = FirebaseAuth.getInstance().uid!!

        model.atividades.observe(this, Observer<HashMap<String, MutableList<Atividade>>>{ atividades ->
            Log.e("teste", atividades!!.toString())
            viewAdapter.eventosFavoritados = atividades
            progressBar.dismiss()
        })
    }

}

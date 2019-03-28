package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.adapters.ListaDeFavoritosAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.ItemLoja
import kotlinx.android.synthetic.main.fragment_minha_semana.*
import java.util.ArrayList

class FragmentMinhaSemana : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val eventosFavoritados = HashMap<String, ArrayList<Atividade>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_minha_semana, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewAdapter = ListaDeFavoritosAdapter(eventosFavoritados)
        val viewManager = LinearLayoutManager(this.context)

        listFavorite_Days.apply {
            adapter = viewAdapter
            layoutManager = viewManager
        }

        val mAuth = FirebaseAuth.getInstance().uid!!
        db.users[mAuth].favorites.get()
            .addOnSuccessListener {
                for(documentSnapshot in it){
                    val weekId = documentSnapshot["weekId"] as String
                    val activityId = documentSnapshot["id"] as String

                    db.weeks[weekId].activities[activityId].get().addOnSuccessListener {
                        val temp = it.toObject(Atividade::class.java)!!
                        temp.inicio.formataSemana().also {
                            if(it !in eventosFavoritados) eventosFavoritados[it] = ArrayList()
                            eventosFavoritados[it]!!.add(temp)
                        }
                        viewAdapter.notifyDataSetChanged()
                    }
                }
            }

    }
}

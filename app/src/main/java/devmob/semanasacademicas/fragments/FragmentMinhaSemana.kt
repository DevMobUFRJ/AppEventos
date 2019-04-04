package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.*
import devmob.semanasacademicas.adapters.ListaDeFavoritosAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.ItemLoja
import kotlinx.android.synthetic.main.fragment_minha_semana.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import java.util.ArrayList

class FragmentMinhaSemana : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val eventosFavoritados = HashMap<String, ArrayList<Atividade>>()
    private lateinit var listener: ListenerRegistration
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

        progressBar.show()
        val mAuth = FirebaseAuth.getInstance().uid!!
        listener = db.users[mAuth].favorites
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    progressBar.dismiss()
                    alert(firebaseFirestoreException.message.toString(), "Opa, algo de errado aconteceu"){
                        okButton {}
                    }
                    return@addSnapshotListener
                } else {
                    for(docChange in querySnapshot!!.documentChanges){
                        val weekId = docChange.document["weekId"] as String
                        val activityId = docChange.document["id"] as String

                        when(docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                db.weeks[weekId].activities[activityId].get().addOnSuccessListener { docSnapshot ->
                                    val temp = docSnapshot.toObject(Atividade::class.java)!!

                                    temp.id = activityId
                                    temp.weekId = weekId

                                    temp.inicio.formataBarra().also {
                                        if(it !in eventosFavoritados) eventosFavoritados[it] = ArrayList()
                                        eventosFavoritados[it]!!.add(temp)
                                    }
                                    viewAdapter.notifyDataSetChanged()
                                    progressBar.dismiss()
                                }
                            }
                            DocumentChange.Type.REMOVED -> {
                                for(entry in eventosFavoritados){
                                    val lista = entry.component2()
                                    val busca = lista.find { it.id == activityId }
                                    if( busca != null){
                                        lista.remove(busca)
                                        if(lista.size < 1) eventosFavoritados.remove(entry.component1())
                                    }
                                }
                                viewAdapter.notifyDataSetChanged()
                                progressBar.dismiss()
                            }
                        }

                    }
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }
}

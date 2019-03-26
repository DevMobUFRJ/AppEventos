package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.adapters.ListaDeEventosAdapter
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.android.synthetic.main.content_tela_principal.*

class FragmentTelaPrincipal : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var listener: ListenerRegistration
    val eventos = mutableListOf<Evento>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.content_tela_principal, null)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAdapter = ListaDeEventosAdapter(eventos)
        viewManager = LinearLayoutManager(this.context)

        recyclerView = lista.apply {
            layoutManager = viewManager
            adapter= viewAdapter
        }

        listener = db.collection("semanas").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                Log.w("aaaaa", "Listen error", firebaseFirestoreException)
                return@addSnapshotListener
            }
            for(doc in querySnapshot!!.documentChanges){

                val temp = doc.document.toObject(Evento::class.java)
                temp.id = doc.document.id

                when(doc.type){
                    DocumentChange.Type.ADDED -> eventos += temp

                    DocumentChange.Type.MODIFIED ->
                        for(i in 0 until eventos.size) if(eventos[i].id == temp.id) eventos[i] = temp

                    DocumentChange.Type.REMOVED -> {
                        var idx: Int? = null
                        for(i in 0 until eventos.size)
                            if(eventos[i].id == temp.id) idx = i
                        idx?.let {
                            eventos.removeAt(it)
                        }
                    }
                }
            }
            viewAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener.remove()
        eventos.clear()
    }
}

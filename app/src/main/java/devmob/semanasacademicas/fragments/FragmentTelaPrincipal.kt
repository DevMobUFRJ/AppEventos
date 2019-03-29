package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.iid.FirebaseInstanceId
import devmob.semanasacademicas.adapters.ListaDeEventosAdapter
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.weeks
import kotlinx.android.synthetic.main.content_tela_principal.*
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class FragmentTelaPrincipal : Fragment() {

    private lateinit var listener: ListenerRegistration
    private val eventos = mutableListOf<Evento>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_principal, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewAdapter = ListaDeEventosAdapter(eventos)
        val viewManager = LinearLayoutManager(this.context)

        lista.apply {
            layoutManager = viewManager
            adapter= viewAdapter
        }

        listener = FirebaseFirestore.getInstance().weeks
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    alert(firebaseFirestoreException.message.toString(), "Opa, algo de errado aconteceu"){
                        okButton {}
                    }
                    return@addSnapshotListener
                }

                for(doc in querySnapshot!!.documentChanges){

                    val temp = doc.document.toObject(Evento::class.java)
                    temp.id = doc.document.id

                    when(doc.type){
                        DocumentChange.Type.ADDED ->
                            eventos += temp

                        DocumentChange.Type.MODIFIED ->
                            for(i in 0 until eventos.size) if(eventos[i].id == temp.id) eventos[i] = temp

                        DocumentChange.Type.REMOVED ->
                            eventos.remove( eventos.find { it.id == temp.id } )
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

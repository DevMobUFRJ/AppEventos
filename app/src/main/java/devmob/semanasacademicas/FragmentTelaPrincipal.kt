package devmob.semanasacademicas

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.card_semana.*
import kotlinx.android.synthetic.main.content_tela_principal.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import java.util.*

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
                    DocumentChange.Type.ADDED -> {
                        Log.w("aaaaa", "add" + doc.document.toString())
                        eventos += temp
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.w("aaaaa", "mod" + doc.document.toString())
                        for(i in 0 until eventos.size)
                            if(eventos[i].id == temp.id) eventos[i] = temp
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.w("aaaaa", "rem" + doc.document.toString())
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
    }
}

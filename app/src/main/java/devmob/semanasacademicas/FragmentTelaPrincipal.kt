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
import com.google.firebase.firestore.FirebaseFirestore
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
    val eventos = mutableListOf<Evento>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_tela_principal, null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewAdapter = ListaDeEventosAdapter(eventos)
        viewManager = LinearLayoutManager(this.context)

        recyclerView = lista.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter= viewAdapter
        }




        db.collection("semanas")
            .orderBy("inicio")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val temp = document.toObject(Evento::class.java)
                    temp.id = document.id
                    eventos += temp
                }
                viewAdapter.notifyDataSetChanged()

            }
            .addOnFailureListener { exception ->
                Log.w("alexlindo", "Error getting documents.", exception)
            }



    }
}

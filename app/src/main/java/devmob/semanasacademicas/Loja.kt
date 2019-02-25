package devmob.semanasacademicas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_loja.*
import java.util.ArrayList

class Loja : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val items = mutableListOf<ItemLoja>()
    lateinit var evento: Evento
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        viewAdapter = ListaLojaAdapter(items)
        viewManager = LinearLayoutManager(this)

        listaLoja.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter= viewAdapter
        }



        items.clear()
        evento = intent?.extras!!.get("EVENTO") as Evento

        db.collection("semanas").document(evento.id).collection("loja")
           .get()
           .addOnSuccessListener { result ->
                Log.e("sei", "la")
                for (document in result) {
                    val temp = document.toObject(ItemLoja::class.java)
                    temp.id = document.id
                    items += temp
                }
               viewAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("alexlindo", "Error getting documents.", exception)
            }


    }
}

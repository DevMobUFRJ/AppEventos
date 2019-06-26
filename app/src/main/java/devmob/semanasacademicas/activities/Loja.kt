package devmob.semanasacademicas.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.adapters.ListaLojaAdapter
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.dataclass.ItemLoja
import kotlinx.android.synthetic.main.activity_loja.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class Loja : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loja)

        val items = mutableListOf<ItemLoja>()
        val evento = intent?.extras!!.get(ARG_EVENT) as Evento

        val viewAdapter = ListaLojaAdapter(items)
        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        listaLoja.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter= viewAdapter
        }

        FirebaseFirestore.getInstance().weeks[evento.id].shop.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val temp = document.toObject(ItemLoja::class.java)
                    temp.id = document.id
                    items += temp
                }
                viewAdapter.notifyDataSetChanged()

            }.addOnFailureListener {
                alert(it.message.toString(), "Opa, algo de errado aconteceu"){
                    okButton {}
                }.show()
            }


    }
}

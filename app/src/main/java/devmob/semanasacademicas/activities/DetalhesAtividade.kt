package devmob.semanasacademicas.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.textResource
import org.jetbrains.anko.toast

class DetalhesAtividade : AppCompatActivity() {

    private lateinit var atividade: Atividade

    private lateinit var weekId: String
    private lateinit var atividadeId: String

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    private lateinit var newFavorite: CollectionReference
    private lateinit var favoriteId: String
    private var favorite = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        atividadeId = intent?.extras!!.getString(ARG_ATIVIDADE_ID)!!
        weekId = intent?.extras!!.getString(ARG_WEEK_ID)!!

        db.weeks[weekId].activities[atividadeId].get()
            .addOnSuccessListener {
                atividade = it.toObject(Atividade::class.java)!!
                atividade.id = atividadeId
                atividade.weekId = weekId

                setViews()

            }.addOnFailureListener {
                alert(it.message.toString(), "Opa, algo de errado aconteceu"){
                    okButton {}
                }.show()
            }

        newFavorite = db.users[mAuth.currentUser!!.uid].favorites
        favoriteOrUnfavorite()
        favoriteThis.setOnClickListener {
            if (favorite) {
                val data = HashMap<String, Any>()

                data[ACTIVITY_ID] = atividadeId
                data[WEEK_ID] = weekId

                newFavorite.add(data).addOnSuccessListener {
                    toast("Favorito adicionado!")
                }
            }
            else
                newFavorite[favoriteId].delete().addOnSuccessListener {
                    toast("Favorito removido")
                }
            favoriteOrUnfavorite()
        }
    }

    private fun favoriteOrUnfavorite() {
        newFavorite.whereEqualTo("id", atividadeId).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    favorite = true
                    favoriteThis.textResource = R.string.favorite_this
                } else {
                    favorite = false
                    favoriteThis.textResource = R.string.unfavorite_this
                    favoriteId = it.documents[0].id
                }

            }.addOnFailureListener {
                alert(it.message.toString(), "Opa, algo de errado aconteceu"){
                    okButton {}
                }.show()
            }
    }

    private fun setViews(){
        tipoAtividade.text = atividade.tipo.formataTipo()
        nomeAtividade.text = atividade.nome
        diaAtividade.text = atividade.inicio.formataDia()
        apresentadorAtividade.text = atividade.apresentador
        empresaAtividade.text = atividade.grupo
        horasalaAtividade.text = getString(R.string.activity_period_and_date, atividade.inicio.formataHora(), atividade.fim.formataHora(), atividade.local)

        if (atividade.tipo == Types.others || atividade.tipo == Types.mesaRedonda)
            cardView.visibility = View.INVISIBLE
        if (atividade.tipo != Types.workshop)
            btnInscrever.visibility = View.INVISIBLE

        btnInscrever.setOnClickListener { view ->
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(this.applicationContext, R.color.colorPrimary))
            val customTabsIntent = builder.build()
            if (!atividade.link.startsWith("https://") && !atividade.link.startsWith("http://"))
                atividade.link = "https://" + atividade.link
            customTabsIntent.launchUrl(view.context, Uri.parse(atividade.link))
        }
    }
}
package devmob.semanasacademicas.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*
import org.jetbrains.anko.*

class DetalhesAtividade : AppCompatActivity() {

    private lateinit var atividade: Atividade

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

        atividade = intent?.extras!!.getParcelable(ARG_ATIVIDADE)!!
        setViews()

        mAuth.addAuthStateListener {
            Log.d("mydebug", "Listener de mudança de estado do usuário (dentro da atividade)")
            if (mAuth.currentUser == null){
                favBtn.setOnClickListener{
                    alert("Você precisa estar logado para acesso a essa função") {
                        negativeButton("Cancelar"){}
                        positiveButton("Entrar"){startActivity<LoginOrRegisterActivity>()}
                    }.show()
                    Log.d("mydebug", "Usuária não logado tentou favoritar atividade")
                }
            }
            else {
                newFavorite = db.users[mAuth.currentUser!!.uid].favorites
                favoriteOrUnfavorite()
                favBtn.setOnClickListener {
                    if (favorite) {
                        val data = HashMap<String, Any>()

                        data[ACTIVITY_ID] = atividade.id
                        data[WEEK_ID] = atividade.weekId

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

        }

    }

    private fun favoriteOrUnfavorite() {
        newFavorite.whereEqualTo("id", atividade.id).get()
            .addOnSuccessListener {
                if (it.isEmpty) {
                    favorite = true
                    favBtn.isFavorited(false)
                } else {
                    favorite = false
                    favBtn.isFavorited(true)
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
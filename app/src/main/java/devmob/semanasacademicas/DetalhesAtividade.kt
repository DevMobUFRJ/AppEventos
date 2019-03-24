package devmob.semanasacademicas

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*
import org.jetbrains.anko.toast

class DetalhesAtividade : AppCompatActivity() {

    lateinit var atividade: Atividade

    lateinit var weekId: String
    lateinit var atividadeId: String

    lateinit var db: FirebaseFirestore
    lateinit var mAuth: FirebaseAuth

    lateinit var newFavorite: CollectionReference
    lateinit var favoriteId: String
    var favorite = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        atividadeId = intent?.extras!!.getString(ARG_ATIVIDADE_ID)!!
        weekId = intent?.extras!!.getString(ARG_WEEK_ID)!!

        db.collection("semanas").document(weekId).collection("atividades").document(atividadeId).get().addOnCompleteListener {
                if(it.isSuccessful){
                    atividade = it.result?.toObject(Atividade::class.java)!!
                    atividade.id = atividadeId
                    atividade.weekId = weekId

                    atividade.run {
                        tipoAtividade.text = tipo.formataTipo()
                        nomeAtividade.text = nome
                        diaAtividade.text = inicio.formataDia()
                        apresentadorAtividade.text = apresentador
                        empresaAtividade.text = grupo
                        horasalaAtividade.text = inicio.formataHora() + " - " + atividade.fim.formataHora() + " | " + atividade.local

                        if (tipo == "outros" || tipo == "mesaRedonda")
                            cardView.visibility = View.INVISIBLE
                        if (tipo != "workshop")
                            btnInscrever.visibility = View.INVISIBLE

                        btnInscrever.setOnClickListener {view ->
                            val builder = CustomTabsIntent.Builder()
                            builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                            val customTabsIntent = builder.build()
                            if(!link.startsWith("https://") && !link.startsWith("http://"))
                                atividade.link = "https://" + atividade.link
                            customTabsIntent.launchUrl(view.context, Uri.parse(atividade.link))
                        }



                    }
                }

            }

        newFavorite = db.collection("users").document(mAuth.currentUser!!.uid).collection("favorites")
        favoriteOrUnfavorite()
        favoriteThis.setOnClickListener{
            if(favorite) addFavorite()
            else removeFavorite()
            favoriteOrUnfavorite()
        }
    }

    private fun addFavorite(){
        val favorite = HashMap<String, Any>()

        favorite["id"] = atividadeId
        favorite["weekId"] = weekId

        newFavorite.add(favorite).addOnSuccessListener {
            toast("Favorito adicionado!")
        }
    }

    private fun removeFavorite(){
        newFavorite.document(favoriteId).delete().addOnCompleteListener {
            if(it.isSuccessful){
                toast("Favorito removido")
            }
        }

    }

    private fun favoriteOrUnfavorite(){
        newFavorite.whereEqualTo("id", atividadeId).get().addOnCompleteListener {
            if(it.isSuccessful){
                if(it.result!!.isEmpty){
                    favorite = true
                    favoriteThis.text = "Favorite essa atividade"
                }
                else{
                    favorite = false
                    favoriteThis.text = "Desfavorite essa atividade"
                    favoriteId = it.result!!.documents[0].id
                    Log.e("aaaaa", favoriteId)
                }
            }
        }
    }
}
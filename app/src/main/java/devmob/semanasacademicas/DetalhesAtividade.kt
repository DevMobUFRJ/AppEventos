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

        atividade = intent?.extras!!.get("ATIVIDADE") as Atividade

        //tipoAtividade.text = atividade.tipo[0].toTitleCase() + atividade.tipo.substring(1)
        atividade.run {
            tipoAtividade.text = tipo.formataTipo()
            nomeAtividade.text = nome
            diaAtividade.text = inicio.formataDia()
            apresentadorAtividade.text = apresentador
            empresaAtividade.text = grupo
            horasalaAtividade.text = inicio.formataHora() + " - " + atividade.fim.formataHora() + " | " + atividade.local
        }

        if (atividade.tipo == "outros" || atividade.tipo == "mesaRedonda")
            cardView.visibility = View.INVISIBLE
        if (atividade.tipo != "workshop")
            btnInscrever.visibility = View.INVISIBLE

        btnInscrever.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            if(!atividade.link.startsWith("https://") && !atividade.link.startsWith("http://"))
                atividade.link = "https://" + atividade.link
            customTabsIntent.launchUrl(this, Uri.parse(atividade.link))
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

        favorite["id"] = atividade.id
        favorite["weekId"] = atividade.weekId

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
        newFavorite.whereEqualTo("id", atividade.id).get().addOnCompleteListener {
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
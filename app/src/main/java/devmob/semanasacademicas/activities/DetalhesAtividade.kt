package devmob.semanasacademicas.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.viewModels.User
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*
import org.jetbrains.anko.*

class DetalhesAtividade : AppCompatActivity() {

    private lateinit var atividade: Atividade

    private lateinit var favoriteId: String
    private var favorite = true

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        atividade = intent?.extras!!.getParcelable(ARG_ATIVIDADE)!!
        setViews()

        user = ViewModelProviders.of(this).get(User::class.java)

        user.logged.observe(this, Observer {
            if(it == true){
                favBtn.setOnClickListener {
                    if (favorite)
                        user.addFavorite(atividade.weekId, atividade.id).addOnSuccessListener {
                            toast("Favorito adicionado!")
                        }
                    else
                        user.removeFavorite(favoriteId).addOnSuccessListener {
                            toast("Favorito removido")
                        }
                    favoriteOrUnfavorite()
                }
            } else {
                favBtn.setOnClickListener{
                    alert("Você precisa estar logado para acesso a essa função") {
                        negativeButton("Cancelar"){}
                        positiveButton("Entrar"){startActivity<LoginActivity>()}
                    }.show()
                }
            }
        })


        user.changed.observe(this, Observer {
            if(it == true) {
                favoriteOrUnfavorite()
                user.changed.value = false
            }
        })
    }

    private fun favoriteOrUnfavorite() {
        user.favorites[atividade.weekId]?.apply {
            val item = filter { it.first == atividade.id }
            if(item.isEmpty()) {
                favorite = true
                favBtn.isFavorited(false)
            } else {
                favorite = false
                favBtn.isFavorited(true)
                favoriteId = item[0].second
            }
        }
    }

    private fun setViews(){
        tipoAtividade.text = atividade.tipo.formataTipo()
        nomeAtividade.text = atividade.nome
        diaAtividade.text = atividade.inicio.formataDia()
        apresentadorAtividade.text = atividade.apresentador
        empresaAtividade.text = atividade.grupo
        horasalaAtividade.text = getString(R.string.activity_period_and_date, atividade.inicio.formataHora(), atividade.fim.formataHora(), atividade.local)

        if (atividade.apresentador == "" || atividade.grupo == "")
            cardView.visibility = View.INVISIBLE
//        if (atividade.tipo != Types.workshop)
//            btnInscrever.visibility = View.INVISIBLE

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
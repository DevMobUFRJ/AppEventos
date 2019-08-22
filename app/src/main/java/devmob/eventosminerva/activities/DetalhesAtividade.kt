package devmob.eventosminerva.activities

import android.graphics.Color
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import android.view.View
import devmob.eventosminerva.*
import devmob.eventosminerva.dataclass.Atividade
import devmob.eventosminerva.viewModels.User
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*
import org.jetbrains.anko.*

class DetalhesAtividade : AppCompatActivity() {

    private lateinit var atividade: Atividade

    private lateinit var favoriteId: String
    private var favorite = true

    lateinit var user: User
    lateinit var color1: String
    lateinit var color2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        atividade = intent?.extras!!.getParcelable(ARG_ATIVIDADE)!!
        color1 = intent?.extras!!.getString(ARG_PRIMARY_COLOR) ?: resources.getString(0+R.color.colorPrimary)
        color2 = intent?.extras!!.getString(ARG_SECUNDARY_COLOR) ?: resources.getString(0+R.color.colorSecondary)

        user = ViewModelProviders.of(this).get(User::class.java)

        setViews()
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
        user.favoriteActivities[atividade.weekId]?.apply {
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
        toolbar.backgroundColor = Color.parseColor(color1)
        nomeAtividade.textColor = Color.parseColor(color1)
        horasalaAtividade.textColor = Color.parseColor(color2)
        btnInscrever.backgroundColor = Color.parseColor(color2)

        val drawableContainerState = favBtn.background.constantState as DrawableContainer.DrawableContainerState
        val children = drawableContainerState.children

//        (children[0] as GradientDrawable).setColor(Color.parseColor(color1))
//        (children[1] as GradientDrawable).setColor(Color.parseColor(color1))
        (children[2] as GradientDrawable).setColor(Color.parseColor(color2))
        (children[3] as GradientDrawable).setColor(Color.parseColor(color2))


        tipoAtividade.text = atividade.tipo.formataTipo()
        nomeAtividade.text = atividade.nome
        diaAtividade.text = atividade.inicio.formataDia()
        horasalaAtividade.text = getString(R.string.activity_period_and_date, atividade.inicio.formataHora(), atividade.fim.formataHora(), atividade.local)

        if(atividade.apresentador.isNotEmpty() || atividade.grupo.isNotEmpty()) {
            apresentadorAtividade.text = if(atividade.apresentador.isNotEmpty()) atividade.apresentador else atividade.grupo
            empresaAtividade.text = if(atividade.apresentador.isNotEmpty()) atividade.grupo else ""

            empresaAtividade.visibility = if (empresaAtividade.text.isEmpty()) View.GONE else View.VISIBLE
        } else cardView.visibility = View.INVISIBLE


        btnInscrever.visibility = if (atividade.inscricao == "n") View.GONE else View.VISIBLE

        btnInscrever.setOnClickListener { view ->
            if (atividade.linkInscricao != ""){
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(Color.parseColor(color1))
                val customTabsIntent = builder.build()
                if (!atividade.linkInscricao.startsWith("https://") && !atividade.linkInscricao.startsWith("http://"))
                    atividade.linkInscricao = "https://" + atividade.linkInscricao
                customTabsIntent.launchUrl(view.context, Uri.parse(atividade.linkInscricao))
            }
            else {
                alert ("Não temos informações sobre a inscrição dessa atividade. Compareça ao credenciamento.", "Ops!"){
                    okButton {  }
                }.show()
            }
        }
    }

}
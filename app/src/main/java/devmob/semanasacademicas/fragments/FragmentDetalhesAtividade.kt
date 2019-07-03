package devmob.semanasacademicas.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dialogs.LoginDialog
import devmob.semanasacademicas.viewModels.SelectedWeek
import devmob.semanasacademicas.viewModels.User
import kotlinx.android.synthetic.main.fragment_detalhes_atividade.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

class FragmentDetalhesAtividade : Fragment() {

    private var favorite: Boolean = false
    lateinit private var favoriteId: String

    lateinit var user: User
    lateinit var selected: SelectedWeek

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_detalhes_atividade, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = ViewModelProviders.of(act).get(User::class.java)
        selected = ViewModelProviders.of(act).get(SelectedWeek::class.java)

        setViews(selected.atividade)

        user.changed.observe(this, Observer {
            favoriteOrUnfavorite()
        })

        user.logged.observe(this, Observer {
            if(it == true){
                favBtn.setOnClickListener {
                    if (favorite)
                        user.addFavorite(selected.atividade.weekId, selected.atividade.id).addOnSuccessListener {
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
                        positiveButton("Entrar"){
                            LoginDialog().show(activity?.supportFragmentManager!!.beginTransaction(), "LoginDialog")
                        }
                    }.show()
                }
            }
        })
    }

    private fun favoriteOrUnfavorite() {
        selected.atividade.let {
            var temp = user.favoriteActivities[it.weekId]?.filter { it.first == selected.atividade.id }
            if(temp.isNullOrEmpty()) {
                favorite = true
                favBtn.isFavorited(false)
            } else {
                favorite = false
                favBtn.isFavorited(true)
                favoriteId = temp[0].second
            }
        }
    }

    private fun setViews(atividade: Atividade){
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
            builder.setToolbarColor(ContextCompat.getColor(this.context!!, R.color.colorPrimary))
            val customTabsIntent = builder.build()
            if (!atividade.link.startsWith("https://") && !atividade.link.startsWith("http://"))
                atividade.link = "https://" + atividade.link
            customTabsIntent.launchUrl(view.context, Uri.parse(atividade.link))
        }
    }
}
package devmob.eventosminerva.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import devmob.eventosminerva.*
import devmob.eventosminerva.activities.AtividadesActivity
import devmob.eventosminerva.activities.TelaPrincipal
import devmob.eventosminerva.adapters.ListaTiposAdapter
import devmob.eventosminerva.dataclass.Atividade
import devmob.eventosminerva.dataclass.Evento
import devmob.eventosminerva.viewModels.WeeksList
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.content_tela_de_evento.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert

class FragmentTelaDeEvento : androidx.fragment.app.Fragment() {

    private lateinit var evento: Evento

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.content_tela_de_evento, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)

        evento = model.item

        (activity!! as TelaPrincipal).apply {
            showSearchButton = false //desabilita o botao de pesquisa
            invalidateOptionsMenu()

            toolbar.title = evento.nome //seta o titulo da toolbar
            toolbar.backgroundColor = Color.parseColor(evento.color1)
        }

        val tipos = listOf(Types.all).plus(evento.listaTipos)
        val viewAdapter = ListaTiposAdapter(tipos, evento)
        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        listaDeTipos.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = viewManager
            adapter = viewAdapter
        }

        FirebaseFirestore.getInstance().weeks[evento.id].activities.get().addOnSuccessListener {
            val temp = mutableListOf<Atividade>()
            for (document in it.documents) {
                val aux = document.toObject(Atividade::class.java)
                aux?.id = document.id
                aux?.weekId = evento.id
                temp.add(aux!!)
            }

            //tipos += temp.groupBy { it.tipo }.keys
            //viewAdapter.notifyDataSetChanged()
//            Log.d("teste", temp.groupBy { it.tipo }.keys.toString())
        }

        evento.run {
            periodoEvento.text = periodo()
            nomeEvento.text = nome
            descricaoEvento.text = descricao

            Glide.with(context!!).load(link)
                .placeholder(R.drawable.thumb_placeholder)
                .error(R.drawable.thumb_error)
                .thumbnail(0.1f)
                .centerCrop()
                .into(imageView)

            nomeEvento.transitionName = resources.getString(R.string.nomeTransition)
            periodoEvento.transitionName = resources.getString(R.string.dateTransition)
            descricaoEvento.transitionName = resources.getString(R.string.descriptionTransition)
            imageView.transitionName = "imagem"
        }

        btnRegister.setOnClickListener {
            if (evento.linkInscricao != ""){
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                if (!evento.linkInscricao.startsWith("https://") && !evento.linkInscricao.startsWith("http://"))
                    evento.linkInscricao = "https://" + evento.linkInscricao
                customTabsIntent.launchUrl(view.context, Uri.parse(evento.linkInscricao))
            }
            else {
                alert ("Não temos informações sobre a inscrição desse evento. Compareça ao credenciamento.", "Ops!"){
                    okButton {  }
                }.show()
            }

        }

    }

    private fun createIntent(type: String) {
        val intent = Intent(this.context, AtividadesActivity::class.java)
        intent.putExtra(ARG_EVENT, evento)
        intent.putExtra(ARG_TYPE, type)
        this.context!!.startActivity(intent)
    }
}

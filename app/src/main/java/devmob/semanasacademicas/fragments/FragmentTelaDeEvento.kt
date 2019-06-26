package devmob.semanasacademicas.fragments

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.Loja
import devmob.semanasacademicas.activities.AtividadesActivity
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.adapters.ListaTiposAdapter
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.content_tela_de_evento.*

class FragmentTelaDeEvento : androidx.fragment.app.Fragment() {

    private lateinit var evento: Evento

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_de_evento, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)

        evento = model.item

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = false //desabilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = evento.nome //seta o titulo da toolbar


        val tipos = listOf(Types.all).plus(evento.listaTipos)
        val viewAdapter = ListaTiposAdapter(tipos, evento)
        val viewManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        listaDeTipos.apply {
            setHasFixedSize(true)
            overScrollMode = View.OVER_SCROLL_NEVER
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

        evento.run{
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



//        btnProgramacao.setOnClickListener {
//            createIntent(Types.all)
//        }
//        btnWorkshops.setOnClickListener {
//            createIntent(Types.workshop)
//        }
//        btnPalestras.setOnClickListener {
//            createIntent(Types.lecture)
//        }
//
//        btnLoja.setOnClickListener {
//            val intent = Intent(this.context, Loja::class.java)
//            intent.putExtra(ARG_EVENT, evento)
//            this.context!!.startActivity(intent)
//        }
    }

    private fun createIntent(type: String){
        val intent = Intent(this.context, AtividadesActivity::class.java)
        intent.putExtra(ARG_EVENT, evento)
        intent.putExtra(ARG_TYPE, type)
        this.context!!.startActivity(intent)
    }
}

package devmob.semanasacademicas.fragments

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.activities.Loja
import devmob.semanasacademicas.R
import devmob.semanasacademicas.Types
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.SelectedWeek
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.content_tela_de_evento.*

class FragmentTelaDeEvento : androidx.fragment.app.Fragment() {

    private lateinit var evento: Evento

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_de_evento, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProviders.of(this.activity!!).get(SelectedWeek::class.java)
        evento = model.selectedWeek

        evento.run{
            periodoEvento.text = periodo()
            nomeEvento.text = nome
            descricaoEvento.text = descricao

            nomeEvento.transitionName = resources.getString(R.string.nomeTransition)
            periodoEvento.transitionName = resources.getString(R.string.dateTransition)
            descricaoEvento.transitionName = resources.getString(R.string.descriptionTransition)
        }

        btnProgramacao.setOnClickListener {
            model.tipo = Types.all
            it.findNavController().navigate(R.id.details_to_atividades)
//            (this.activity as TelaPrincipal).displayScreen(30)
            //createIntent(Types.all)
        }
        btnWorkshops.setOnClickListener {
            model.tipo = Types.workshop
            it.findNavController().navigate(R.id.details_to_atividades)
//            (this.activity as TelaPrincipal).displayScreen(30)
            //createIntent(Types.workshop)
        }
        btnPalestras.setOnClickListener {
            model.tipo = Types.lecture
            it.findNavController().navigate(R.id.details_to_atividades)
//            (this.activity as TelaPrincipal).displayScreen(30)
            //createIntent(Types.lecture)
        }

        btnLoja.setOnClickListener {
            val intent = Intent(this.context, Loja::class.java)
            intent.putExtra(ARG_EVENT, evento)
            this.context!!.startActivity(intent)
        }
    }
}

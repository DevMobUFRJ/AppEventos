package devmob.semanasacademicas.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.ARG_TYPE
import devmob.semanasacademicas.activities.Loja
import devmob.semanasacademicas.R
import devmob.semanasacademicas.Types
import devmob.semanasacademicas.activities.AtividadesActivity
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.content_tela_de_evento.*

class FragmentTelaDeEvento : Fragment() {

    private lateinit var evento: Evento

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_de_evento, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)

        evento = model.item
        evento.run{
            periodoEvento.text = periodo()
            nomeEvento.text = nome
            descricaoEvento.text = descricao
        }

        btnProgramacao.setOnClickListener {
            createIntent(Types.all)
        }
        btnWorkshops.setOnClickListener {
            createIntent(Types.workshop)
        }
        btnPalestras.setOnClickListener {
            createIntent(Types.lecture)
        }

        btnLoja.setOnClickListener {
            val intent = Intent(this.context, Loja::class.java)
            intent.putExtra(ARG_EVENT, evento)
            this.context!!.startActivity(intent)
        }
    }

    private fun createIntent(type: String){
        val intent = Intent(this.context, AtividadesActivity::class.java)
        intent.putExtra(ARG_EVENT, evento)
        intent.putExtra(ARG_TYPE, type)
        this.context!!.startActivity(intent)
    }
}

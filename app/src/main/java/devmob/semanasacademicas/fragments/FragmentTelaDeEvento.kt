package devmob.semanasacademicas.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.semanasacademicas.activities.Loja
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.AtividadesActivity
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.android.synthetic.main.content_tela_de_evento.*

class FragmentTelaDeEvento : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_tela_de_evento, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //pega o evento
        val bundle = arguments
        val evento = bundle!!.getParcelable<Evento>("EVENTO")

//        val meses = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
//        var diaInicio = evento.inicio.toDate().date
//        var mesInicio = evento.inicio.toDate().month
//        var diaFim = evento.fim.toDate().date
//        var mesFim = evento.fim.toDate().month
//
//        periodoEvento.text = if (mesFim == mesInicio) "$diaInicio a $diaFim de ${meses[mesInicio]}" else "$diaInicio de ${meses[mesInicio]} a $diaFim de ${meses[mesFim]}"
//        nomeEvento.text = evento.nome
//        descricaoEvento.text = evento.descricao

        evento?.run{
            periodoEvento.text = periodo()
            nomeEvento.text = nome
            descricaoEvento.text = descricao
        }

        btnProgramacao.setOnClickListener {
            val intent = Intent(this.context, AtividadesActivity::class.java)
            intent.putExtra("EVENTO",evento)
            intent.putExtra("TIPO", "todos")
            this.context!!.startActivity(intent)
        }
        btnWorkshops.setOnClickListener {
            val intent = Intent(this.context, AtividadesActivity::class.java)
            intent.putExtra("EVENTO",evento)
            intent.putExtra("TIPO", "workshop")
            this.context!!.startActivity(intent)
        }
        btnPalestras.setOnClickListener {
            val intent = Intent(this.context, AtividadesActivity::class.java)
            intent.putExtra("EVENTO",evento)
            intent.putExtra("TIPO", "palestra")
            this.context!!.startActivity(intent)
        }

        btnLoja.setOnClickListener {
            val intent = Intent(this.context, Loja::class.java)
            intent.putExtra("EVENTO",evento)
            this.context!!.startActivity(intent)
        }
    }
}

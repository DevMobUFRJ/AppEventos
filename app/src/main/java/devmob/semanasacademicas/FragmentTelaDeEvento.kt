package devmob.semanasacademicas

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_tela_de_evento.*
import java.time.LocalDate
import java.util.*

class FragmentTelaDeEvento : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_tela_de_evento, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //pega o evento
        var bundle = arguments
        var evento = bundle!!.getParcelable<Evento>("EVENTO")

        val meses = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
        var diaInicio = evento.inicio.toDate().date
        var mesInicio = evento.inicio.toDate().month
        var diaFim = evento.fim.toDate().date
        var mesFim = evento.fim.toDate().month

        periodoEvento.text = if (mesFim == mesInicio) "$diaInicio a $diaFim de ${meses[mesInicio]}" else "$diaInicio de ${meses[mesInicio]} a $diaFim de ${meses[mesFim]}"
        nomeEvento.text = evento.nome
        descricaoEvento.text = evento.descricao

        btnProgramacao.setOnClickListener {
            val intent = Intent(this.context, AtividadesActivity::class.java)
            intent.putExtra("EVENTO",evento)
            this.context!!.startActivity(intent)
        }



    }
}

package devmob.semanasacademicas

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_tela_de_evento.*

class FragmentTelaDeEvento : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_tela_de_evento, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //pega o evento
        var bundle = arguments
        var evento = bundle!!.getParcelable<Evento>("EVENTO")

        nomeEvento.text = evento.nome
        descricaoEvento.text = evento.descricao

    }
}

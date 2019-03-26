package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.semanasacademicas.R

class FragmentMinhaSemana : Fragment() {
    //essa parte aqui eh padrao pra todos fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_minha_semana, null) //R.layout.[layout_do_fragment]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //AQUI VAI O CODIGO
    }
}

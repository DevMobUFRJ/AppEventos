package devmob.semanasacademicas.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import kotlinx.android.synthetic.main.app_bar_tela_principal.*

class SettingsFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parentActivity = activity!! as TelaPrincipal
        parentActivity.showSearchButton = false //desabilita o botao de pesquisa
        parentActivity.invalidateOptionsMenu()
        parentActivity.toolbar.title = "Configurações" //seta o titulo da toolbar



    }
}

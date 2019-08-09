package devmob.eventosminerva.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import devmob.eventosminerva.R
import devmob.eventosminerva.activities.TelaPrincipal
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_settings.*

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


        val sharedPref = parentActivity.getSharedPreferences("NOTIFICATION", Context.MODE_PRIVATE)
        val show = sharedPref.getBoolean("onoffNotification", true)
        switch1.isChecked = show

        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            with(sharedPref.edit()){
                putBoolean("onoffNotification", isChecked)
                apply()
            }
        }


    }
}

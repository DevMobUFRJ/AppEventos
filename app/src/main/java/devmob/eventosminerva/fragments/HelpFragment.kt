package devmob.eventosminerva.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import devmob.eventosminerva.R
import devmob.eventosminerva.activities.TelaPrincipal
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_help.*

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity!! as TelaPrincipal).apply {
            showSearchButton = false //desabilita o botao de pesquisa
            invalidateOptionsMenu()

            toolbar.title = "Ajuda"
        }

        github.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(view.context, Uri.parse("https://github.com/devmobufrj"))

        }
        facebook.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(view.context, Uri.parse("https://facebook.com/devmobufrj"))

        }

    }
}

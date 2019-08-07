package devmob.semanasacademicas.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.fragment_help.*
import org.jetbrains.anko.backgroundColor

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

        help.text = SpannableStringBuilder()
            .bold{ append(resources.getString(R.string.app_name)).append("\n")}
            .append(resources.getString(R.string.app_description)).append("\n\n")
            .bold { append(resources.getString(R.string.group_name)).append("\n") }
            .append(resources.getString(R.string.group_description)).append("\n")
    }
}

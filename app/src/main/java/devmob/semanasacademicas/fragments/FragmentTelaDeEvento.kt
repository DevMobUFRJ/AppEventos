package devmob.semanasacademicas.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.R
import devmob.semanasacademicas.Types
import devmob.semanasacademicas.activities.Loja
import devmob.semanasacademicas.databinding.ContentEventDetailsBinding
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.SelectedWeek
import kotlinx.android.synthetic.main.content_event_details.*

class FragmentTelaDeEvento : androidx.fragment.app.Fragment() {

    private lateinit var evento: Evento
    lateinit var model: SelectedWeek

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?
            = ContentEventDetailsBinding.inflate(layoutInflater, container, false).run {
        model = ViewModelProviders.of(activity!!).get(SelectedWeek::class.java)
        event = model.selectedWeek
        fragment = this@FragmentTelaDeEvento
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLoja.setOnClickListener {
            val intent = Intent(this.context, Loja::class.java)
            intent.putExtra(ARG_EVENT, evento)
            this.context!!.startActivity(intent)
        }
    }

    fun onTypeClicked(view: View, types: String){
        model.tipo = types
        view.findNavController().navigate(R.id.details_to_atividades)
    }
}
package devmob.semanasacademicas.adapters
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.android.synthetic.main.filter_type.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class TypeListAdapter(
    val types: MutableList<String>,
    val evento: Evento,
    val context: Context?
): RecyclerView.Adapter<TypeListAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.filter_type, p0, false))

    override fun getItemCount() = types.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(types[p1])

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        private val tipo = itemView.type as Button

        fun bindItems(tipo: String) {
            this.tipo.text = tipo.capitalize()
            this.tipo.setOnClickListener {
                (it.context as TelaPrincipal).activitiesList.tipo = tipo
                it.findNavController().navigate(R.id.details_to_atividades)
            }
        }
    }

}

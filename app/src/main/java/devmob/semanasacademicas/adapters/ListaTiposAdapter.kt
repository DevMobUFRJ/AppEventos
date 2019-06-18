package devmob.semanasacademicas.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.ARG_TYPE
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.AtividadesActivity
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.android.synthetic.main.card_tipo_atividade.view.*

class ListaTiposAdapter(private val items: MutableList<String>, private val evento: Evento): RecyclerView.Adapter<ListaTiposAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.card_tipo_atividade, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(items[p1], evento)

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        private val tipo = itemView.tipo as TextView
        fun bindItems(tipo: String, evento: Evento) {
            this.tipo.text = tipo.capitalize()
            itemView.setOnClickListener {
                val intent = Intent(it.context, AtividadesActivity::class.java)
                intent.putExtra(ARG_EVENT, evento)
                intent.putExtra(ARG_TYPE, tipo)
                it.context.startActivity(intent)
            }
        }
    }
}
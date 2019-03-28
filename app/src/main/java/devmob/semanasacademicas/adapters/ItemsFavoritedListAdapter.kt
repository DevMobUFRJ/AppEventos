package devmob.semanasacademicas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.Atividade
import kotlinx.android.synthetic.main.minha_semana_item.view.*

class ItemsFavoritedListAdapter(private val listActivities: ArrayList<Atividade>) : RecyclerView.Adapter<ItemsFavoritedListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.minha_semana_item, p0, false))

    override fun getItemCount() = listActivities.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(listActivities[p1])

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val teste1 = itemView.textView
        val teste2 = itemView.textView2

        fun bindItems(item: Atividade) {
            teste1.text = item.nome
            teste2.text = item.apresentador
        }

    }
}

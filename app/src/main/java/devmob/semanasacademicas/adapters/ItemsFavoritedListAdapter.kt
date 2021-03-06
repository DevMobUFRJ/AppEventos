package devmob.semanasacademicas.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.DetalhesAtividade
import devmob.semanasacademicas.dataclass.Atividade
import kotlinx.android.synthetic.main.minha_semana_item.view.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.properties.Delegates

class ItemsFavoritedListAdapter : RecyclerView.Adapter<ItemsFavoritedListAdapter.ViewHolder>() {
    var listActivities by Delegates.observable(mutableListOf<Atividade>()) { _, old, new ->
        notifyChanges(old, new)
    }

    private fun notifyChanges(old: MutableList<Atividade>, new: MutableList<Atividade>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun areContentsTheSame(p0: Int, p1: Int) = old[p0] == new[p1]

            override fun areItemsTheSame(p0: Int, p1: Int) = old[p0].id == new[p1].id

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size

        })

        diff.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.minha_semana_item, p0, false))

    override fun getItemCount() = listActivities.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(listActivities[p1])

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val teste1 = itemView.textView
        private val teste2 = itemView.textView2

        fun bindItems(item: Atividade) {
            teste1.text = item.nome
            teste2.text = item.inicio.formataHora() + "h | " + item.local
            itemView.setOnClickListener {
                if(item.tipo != Types.pause)
                    it.context.startActivity<DetalhesAtividade>(
                        ARG_ATIVIDADE to item
                    )
            }
        }

    }
}

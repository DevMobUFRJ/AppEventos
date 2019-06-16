package devmob.semanasacademicas.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.formataMes
import kotlinx.android.synthetic.main.minha_semana_dia.view.*
import kotlin.properties.Delegates

class ListaDeFavoritosAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ListaDeFavoritosAdapter.ViewHolder>() {
    var eventosFavoritados by Delegates.observable(hashMapOf<String, MutableList<Atividade>>()) { _, oldValue, newValue ->
        notifyChanges(oldValue, newValue)
    }

    private fun notifyChanges(oldValue: HashMap<String, MutableList<Atividade>>, newValue: HashMap<String, MutableList<Atividade>>) {

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            val keys1 = oldValue.keys.toList().sorted()
            val keys2 = newValue.keys.toList().sorted()

            override fun areItemsTheSame(p0: Int, p1: Int) = keys1[p0] == keys2[p1]

            override fun areContentsTheSame(p0: Int, p1: Int) = oldValue[keys1[p0]]?.equals(newValue[keys2[p1]]) == true

            override fun getOldListSize() = oldValue.size

            override fun getNewListSize() = newValue.size

        })
        diff.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.minha_semana_dia, p0, false))

    override fun getItemCount() = eventosFavoritados.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int){
        val keys = eventosFavoritados.keys.toList().sorted()
        val listActivities = eventosFavoritados[keys[p1]]!!
        p0.bindItems(listActivities)
    }

    class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        private val dia = itemView.dia_numero as TextView
        private val diaSemana = itemView.mes as TextView
        private val list = itemView.listFavorite_Items as androidx.recyclerview.widget.RecyclerView

        init {
            list.apply {
                adapter = ItemsFavoritedListAdapter()
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)
            }
        }

        fun bindItems(listActivities: MutableList<Atividade>){
            if(listActivities.size == 0) return

            val temp = listActivities[0].inicio.formataMes()
            dia.text = temp.subSequence(0, 2)
            diaSemana.text = temp.subSequence(3, 6)

            listActivities.sortBy { it.inicio }

            val adp = list.adapter as ItemsFavoritedListAdapter
            adp.listActivities = listActivities
        }

    }

}

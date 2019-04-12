package devmob.semanasacademicas.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

class ListaDeFavoritosAdapter : RecyclerView.Adapter<ListaDeFavoritosAdapter.ViewHolder>() {
    var eventosFavoritados by Delegates.observable(hashMapOf<String, ArrayList<Atividade>>()) { _, oldValue, newValue ->
        notifyChanges(oldValue, newValue)
    }

    private fun notifyChanges(oldValue: HashMap<String, ArrayList<Atividade>>, newValue: HashMap<String, ArrayList<Atividade>>) {

        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun areItemsTheSame(p0: Int, p1: Int): Boolean  {
                val keys1 = oldValue.keys.toList().sorted()
                val keys2 = newValue.keys.toList().sorted()

                return keys1[p0] == keys2[p1]
            }

            override fun areContentsTheSame(p0: Int, p1: Int): Boolean  {
                val keys1 = oldValue.keys.toList().sorted()
                val keys2 = newValue.keys.toList().sorted()

                return oldValue[keys1[p0]]?.equals(newValue[keys2[p1]]) == true
            }

            override fun getOldListSize() = oldValue.size

            override fun getNewListSize() = newValue.size

        })
        diff.dispatchUpdatesTo(this)

        Log.e("aaaa", "old: $oldValue")
        Log.e("aaaa", "new: $newValue\n")
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.minha_semana_dia, p0, false))

    override fun getItemCount() = eventosFavoritados.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int){
        val keys = eventosFavoritados.keys.toList().sorted()
        val listActivities = eventosFavoritados[keys[p1]]!!
        p0.bindItems(listActivities)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val dia = itemView.dia_numero as TextView
        private val diaSemana = itemView.mes as TextView
        private val list = itemView.listFavorite_Items as RecyclerView


        fun bindItems(listActivities: ArrayList<Atividade>){
            if(listActivities.size == 0) return

            val temp = listActivities[0].inicio.formataMes()
            dia.text = temp.subSequence(0, 2)
            diaSemana.text = temp.subSequence(3, 6)

            listActivities.sortBy { it.inicio }

            list.apply {
                adapter = ItemsFavoritedListAdapter(listActivities)
                layoutManager = LinearLayoutManager(this.context)
            }

        }

    }

}

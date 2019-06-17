package devmob.semanasacademicas.adapters

import androidx.transition.*
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_semana.view.*
import androidx.recyclerview.widget.DiffUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentTelaDeEvento
import kotlin.properties.Delegates


class ListaDeEventosAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<ListaDeEventosAdapter.ViewHolder>(){

    var eventos by Delegates.observable(mutableListOf<Evento>()) { _, oldValue, newValue ->
        notifyChanges(oldValue, newValue)
    }

    private fun notifyChanges(oldValue: MutableList<Evento>, newValue: MutableList<Evento>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun areItemsTheSame(p0: Int, p1: Int) = oldValue[p0].id == newValue[p1].id

            override fun areContentsTheSame(p0: Int, p1: Int) = oldValue[p0] == newValue[p1]

            override fun getOldListSize() = oldValue.size

            override fun getNewListSize() = newValue.size
        })
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.card_semana, p0, false))

    override fun getItemCount() = eventos.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(eventos[p1])

    class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        private val nome = itemView.nomePagPrincipal as TextView
        private val descricao = itemView.descPagPrincipal as TextView
        private val periodo = itemView.dataPagPrincipal as TextView
        private val botao = itemView.botaoAbrirEvento as Button
        private val imageView = itemView.imageView2 as ImageView

        fun bindItems(event: Evento){
            Glide.with(itemView).load(event.link)
                .placeholder(R.drawable.thumb_placeholder)
                .error(R.drawable.thumb_error)
                .thumbnail(0.1f)
                .into(imageView)

            nome.text = event.nome
            descricao.text = event.descricao
            periodo.text = event.periodo()

            botao.setOnClickListener {
                val activity = it.context as TelaPrincipal

                activity.activitiesList.run {
                    if(event is Atividade) {
                        atividade = event
                        it.findNavController().navigate(R.id.evento_to_detailsAtividade)
                    } else {
                        selectedWeek = event
                        it.findNavController().navigate(R.id.eventos_to_details)
                    }
                }
                activity.weeksList.screen = 20

            }

        }
    }

}
package devmob.semanasacademicas.adapters

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_semana.view.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentTelaDeEvento
import org.jetbrains.anko.activityManager
import kotlin.properties.Delegates


class ListaDeEventosAdapter: RecyclerView.Adapter<ListaDeEventosAdapter.ViewHolder>(){

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

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val nome = itemView.nomePagPrincipal as TextView
        private val descricao = itemView.descPagPrincipal as TextView
        private val periodo = itemView.dataPagPrincipal as TextView
        private val botao = itemView.botaoAbrirEvento as Button
        private val imageView = itemView.imageView2 as ImageView

        fun bindItems(event: Evento){
            Glide.with(itemView).load(event.link).thumbnail(0.1f).into(imageView)

            nome.text = event.nome
            descricao.text = event.descricao
            periodo.text = event.periodo()

            botao.setOnClickListener {
                val activity = it.context as AppCompatActivity
                val fragment = FragmentTelaDeEvento()
                fragment.retainInstance = true

                val bundle = Bundle()
                bundle.putParcelable(ARG_EVENT, event)
                fragment.arguments = bundle
                activity.supportFragmentManager.beginTransaction().replace(R.id.contentHome, fragment)
                    .addToBackStack(null).commit()
            }

        }
    }

}
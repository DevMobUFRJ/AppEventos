package devmob.semanasacademicas.adapters

import android.os.Bundle
import android.support.transition.*
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
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentTelaDeEvento
import kotlinx.android.synthetic.main.content_tela_de_evento.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.ctx
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
                val activity = it.context as TelaPrincipal

                activity.model.item = event
                activity.model.screen = 20

                val befo = activity.supportFragmentManager.findFragmentById(R.id.contentHome)
                val next = FragmentTelaDeEvento()

                nome.transitionName = activity.resources.getString(R.string.nomeTransition)
                periodo.transitionName = activity.resources.getString(R.string.dateTransition)
                descricao.transitionName = activity.resources.getString(R.string.descriptionTransition)
                
                //exit
                befo?.exitTransition = Fade().apply {
                    duration = 150
                }

                //enter
                next.enterTransition = Fade().apply {
                    duration = 150
                    startDelay = 150
                }

                next.returnTransition = Fade().apply {
                    duration = 150
                }

                befo?.reenterTransition = Fade().apply {
                    startDelay = 50
                    duration = 150
                }
                //common
                next.sharedElementEnterTransition = TransitionSet().apply {
                    addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move).apply {
                        startDelay = 100
                        duration = 100
                        addTarget(activity.resources.getString(R.string.dateTransition))
                    })
                    addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move).apply {
                        startDelay = 100
                        duration = 130
                        addTarget(activity.resources.getString(R.string.nomeTransition))
                    })
                    addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.move).apply {
                        startDelay = 100
                        duration = 190
                        addTarget(activity.resources.getString(R.string.descriptionTransition))
                    })
                }

                activity.supportFragmentManager.beginTransaction().apply {
                    addSharedElement(nome, nome.transitionName)
                    addSharedElement(periodo, periodo.transitionName)
                    addSharedElement(descricao, descricao.transitionName)
                    replace(R.id.contentHome, next)
                    addToBackStack(next.javaClass.name)
                    commit()
                }

                //activity.displayScreen(20)
            }

        }
    }

}
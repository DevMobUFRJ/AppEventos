package devmob.semanasacademicas.adapters

import android.os.Bundle
import androidx.transition.*
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_semana.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.TelaPrincipal
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentTelaDeEvento
import kotlinx.android.synthetic.main.content_tela_de_evento.*
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk27.coroutines.onClick
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
        private val imageView = itemView.imageView2 as ImageView
        private val but = itemView.botaoFavoritar as Button

        fun bindItems(event: Evento){
            Glide.with(itemView).load(event.link)
                .placeholder(R.drawable.thumb_placeholder)
                .error(R.drawable.thumb_error)
                .thumbnail(0.1f)
                .fitCenter()
                .into(imageView)

            nome.text = event.nome
            descricao.text = event.descricao
            periodo.text = event.periodo()

            (itemView.context as TelaPrincipal).run {
                user.changed.observe(this, Observer {
                    val isFavorited = event.id in user.favoriteEvents
                    but.text = if(isFavorited) "des" else "fav"
                    but.onClick {
                        if (isFavorited) user.favoriteEvents[event.id]?.run { user.removeFavorite(this) }
                        else user.addFavorite(event.id, "")
                    }
                })
            }

            itemView.setOnClickListener {
                val activity = it.context as TelaPrincipal
                Log.d("mydebug", event.listaTipos.toString())

                activity.model.item = event
                activity.model.screen = 20

                val befo = activity.supportFragmentManager.findFragmentById(R.id.contentHome)
                val next = FragmentTelaDeEvento()

                nome.transitionName = activity.resources.getString(R.string.nomeTransition)
                periodo.transitionName = activity.resources.getString(R.string.dateTransition)
                descricao.transitionName = activity.resources.getString(R.string.descriptionTransition)
                imageView.transitionName = "imagem"

                //exit
                befo?.exitTransition = Fade().apply {
                    duration = 200
                }

                //enter
                next.enterTransition = Fade().apply {
                    duration = 200
                    startDelay = 100
                }

                next.returnTransition = Fade().apply {
                    duration = 200
                }

                befo?.reenterTransition = Fade().apply {
                    startDelay = 100
                    duration = 200
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
                activity.showSearchButton = false
                activity.invalidateOptionsMenu()
                //activity.displayScreen(20)
            }

        }
    }

}
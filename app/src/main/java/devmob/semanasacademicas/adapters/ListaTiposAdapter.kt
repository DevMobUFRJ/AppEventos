package devmob.semanasacademicas.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import devmob.semanasacademicas.ARG_EVENT
import devmob.semanasacademicas.ARG_TYPE
import devmob.semanasacademicas.R
import devmob.semanasacademicas.activities.AtividadesActivity
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.android.synthetic.main.card_tipo_atividade.view.*
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableContainer.DrawableContainerState

class ListaTiposAdapter(private val items: List<String>, private val evento: Evento): androidx.recyclerview.widget.RecyclerView.Adapter<ListaTiposAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.card_tipo_atividade, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(items[p1], evento)

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val tipo = itemView.tipo as Button
        fun bindItems(tipo: String, evento: Evento) {
            this.tipo.text = tipo.toUpperCase()


//            val gradientDrawable = inflatedView.getBackground() as StateListDrawable
            val drawableContainerState = this.tipo.background.constantState as DrawableContainerState
            val children = drawableContainerState.children

            val unselectedItem = children[0] as GradientDrawable
            unselectedItem.setStroke(2, Color.parseColor(evento.color2))

            val selectedItem = children[1] as GradientDrawable
            selectedItem.setStroke(2, Color.parseColor(evento.color2))
            selectedItem.setColor(Color.parseColor(evento.color2))

            itemView.tipo.setOnClickListener {
                val intent = Intent(it.context, AtividadesActivity::class.java)
                intent.putExtra(ARG_EVENT, evento)
                intent.putExtra(ARG_TYPE, tipo)
                it.context.startActivity(intent)
            }
        }
    }
}
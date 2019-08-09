package devmob.eventosminerva.adapters

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import devmob.eventosminerva.*
import devmob.eventosminerva.activities.DetalhesAtividade
import devmob.eventosminerva.dataclass.Atividade
import org.jetbrains.anko.*
import kotlinx.android.synthetic.main.atividades_lista_items.view.*

class AtividadesListAdapter(
    private val items: List<Atividade>,
    private val color1: String,
    private val color2: String
): RecyclerView.Adapter<AtividadesListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.atividades_lista_items, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItens(items[p1])


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val hora = itemView.itemListaHora as TextView
        private val tipo = itemView.itemListaTipo as TextView
        private val nome = itemView.itemListaNome as TextView

        fun bindItens(atividade: Atividade){
            hora.text = itemView.context.getString(R.string.activity_period,
                atividade.inicio.formataHora(), atividade.fim.formataHora())
            hora.textColor = Color.parseColor(color2)

            nome.text = atividade.nome
            nome.textColor = Color.parseColor(color1)

            tipo.text = atividade.tipo.formataTipo()

            itemView.setOnClickListener {
                if(atividade.tipo != Types.pause)
                    it.context.startActivity<DetalhesAtividade>(
                        ARG_ATIVIDADE to atividade,
                        ARG_PRIMARY_COLOR to color1,
                        ARG_SECUNDARY_COLOR to color2
                    )
            }
        }
    }
}
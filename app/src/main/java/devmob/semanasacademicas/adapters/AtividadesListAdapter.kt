package devmob.semanasacademicas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import devmob.semanasacademicas.*
import devmob.semanasacademicas.activities.DetalhesAtividade
import devmob.semanasacademicas.dataclass.Atividade
import org.jetbrains.anko.*
import kotlinx.android.synthetic.main.atividades_lista_items.view.*

class AtividadesListAdapter(private val items: List<Atividade>): RecyclerView.Adapter<AtividadesListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.atividades_lista_items, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItens(items[p1])


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val hora = itemView.itemListaHora as TextView
        private val tipo = itemView.itemListaTipo as TextView
        private val nome = itemView.itemListaNome as TextView

        fun bindItens(atividade: Atividade){

            hora.text = itemView.context.getString(R.string.activity_period,
                atividade.inicio.formataHora(), atividade.fim.formataHora())
            nome.text = atividade.nome
            tipo.text = atividade.tipo.formataTipo()

            itemView.setOnClickListener {
                if(atividade.tipo != Types.pause)
                    it.context.startActivity<DetalhesAtividade>(
                        ARG_ATIVIDADE_ID to atividade.id,
                        ARG_WEEK_ID to atividade.weekId
                    )
            }
        }
    }
}
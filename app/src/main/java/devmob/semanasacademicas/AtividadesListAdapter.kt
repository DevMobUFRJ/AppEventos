package devmob.semanasacademicas

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.atividades_lista_items.view.*

class AtividadesListAdapter(private val items: List<Atividade>): RecyclerView.Adapter<AtividadesListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        AtividadesListAdapter.ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.atividades_lista_items, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItens(items[p1])


    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val hora = itemView.itemListaHora
        val tipo = itemView.itemListaTipo
        val nome = itemView.itemListaNome

        fun bindItens(atividade: Atividade){
            hora.text = atividade.inicio.formataHora() + "-" + atividade.fim.formataHora()
            nome.text = atividade.nome
            tipo.text = atividade.tipo.formataTipo()

            itemView.setOnClickListener {
                if(atividade.tipo != "intervalo") {
                    val intent = Intent(it.context, DetalhesAtividade::class.java)
                    intent.putExtra("ATIVIDADE", atividade)
                    it.context.startActivity(intent)
                }
            }
        }
    }


}
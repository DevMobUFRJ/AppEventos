package devmob.semanasacademicas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.ItemLoja
import devmob.semanasacademicas.format
import kotlinx.android.synthetic.main.item_loja.view.*

class ListaLojaAdapter(private val items: MutableList<ItemLoja>): RecyclerView.Adapter<ListaLojaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(
            LayoutInflater.from(p0.context).inflate(
                R.layout.item_loja,
                p0,
                false
            )
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(p0.view).load(items[p1].link).thumbnail(0.1f).into(p0.foto)
        //DownloadImage(p0.foto).execute(items[p1].link)
        p0.nome.text = items[p1].nome
        p0.preco.text = "R$" + items[p1].preco.format(2)
        p0.desc.text = items[p1].descricao

    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val nome = itemView.nomeItemLoja
        val preco = itemView.precoItemLoja
        val desc = itemView.descItemLoja
        val foto = itemView.fotoItemLoja
        val view = itemView
    }


}
package devmob.semanasacademicas.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import devmob.semanasacademicas.R
import devmob.semanasacademicas.dataclass.ItemLoja
import devmob.semanasacademicas.format
import kotlinx.android.synthetic.main.item_loja.view.*

class ListaLojaAdapter(private val items: MutableList<ItemLoja>): androidx.recyclerview.widget.RecyclerView.Adapter<ListaLojaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_loja, p0, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) = p0.bindItems(items[p1])

    class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val nome = itemView.nomeItemLoja as TextView
        private val preco = itemView.precoItemLoja as TextView
        private val desc = itemView.descItemLoja as TextView
        private val foto = itemView.fotoItemLoja as ImageView

        fun bindItems(produto: ItemLoja) {
            Glide.with(itemView).load(produto.link).thumbnail(0.1f).into(foto)
            nome.text = produto.nome
            preco.text = itemView.context.getString(R.string.money, produto.preco.format(2))
            desc.text = produto.descricao
        }
    }


}
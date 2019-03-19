package devmob.semanasacademicas

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_semana.view.*
import java.util.*
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide


class ListaDeEventosAdapter(private val eventos: MutableList<Evento>): RecyclerView.Adapter<ListaDeEventosAdapter.ViewHolder>(){

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        Glide.with(p0.view).load(eventos[p1].link).thumbnail(0.1f).into(p0.imageView)
        //DownloadImage(p0.imageView).execute(eventos[p1].link)

        p0.nome.text = eventos[p1].nome
        p0.descricao.text = eventos[p1].descricao
        p0.periodo.text = eventos[p1].periodo()

        p0.botao.setOnClickListener {
            //val intent = Intent(it.context, TelaDeEvento::class.java)
            //intent.putExtra("EVENTO",eventos[p1])
            //it.context.startActivity(intent)

            val activity = it.context as AppCompatActivity
            val fragment = FragmentTelaDeEvento()
            val bundle = Bundle()
            bundle.putParcelable("EVENTO", eventos[p1])
            fragment.arguments = bundle
            activity.supportFragmentManager.beginTransaction().replace(R.id.contentHome, fragment)
                .addToBackStack(null).commit()



        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) =
        ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.card_semana, p0, false))

    override fun getItemCount() = eventos.size

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val nome = itemView.nomePagPrincipal!!
        val descricao = itemView.descPagPrincipal!!
        val periodo = itemView.dataPagPrincipal!!
        val botao = itemView.botaoAbrirEvento!!
        val imageView = itemView.imageView2!!
        val view = itemView
    }

}
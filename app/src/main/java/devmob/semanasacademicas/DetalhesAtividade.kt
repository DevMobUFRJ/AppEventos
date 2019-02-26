package devmob.semanasacademicas

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.view.View
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*

class DetalhesAtividade : AppCompatActivity() {

    lateinit var atividade: Atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        atividade = intent?.extras!!.get("ATIVIDADE") as Atividade

        //tipoAtividade.text = atividade.tipo[0].toTitleCase() + atividade.tipo.substring(1)
        tipoAtividade.text = atividade.tipo.formataTipo()
        nomeAtividade.text = atividade.nome
        diaAtividade.text = atividade.inicio.formataDia()
        horasalaAtividade.text = atividade.inicio.formataHora() + " - " + atividade.fim.formataHora() + " | " + atividade.local
        apresentadorAtividade.text = atividade.apresentador
        empresaAtividade.text = atividade.grupo

        if (atividade.tipo == "outros" || atividade.tipo == "mesaRedonda"){
            cardView.visibility = View.INVISIBLE
        }
        if (atividade.tipo != "workshop"){
            btnInscrever.visibility = View.INVISIBLE
        }

        btnInscrever.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            if(!atividade.link.startsWith("https://") && !atividade.link.startsWith("http://"))
                atividade.link = "https://" + atividade.link
            customTabsIntent.launchUrl(this, Uri.parse(atividade.link))
        }

    }
}

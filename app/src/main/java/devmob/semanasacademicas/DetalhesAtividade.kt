package devmob.semanasacademicas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_detalhes_atividade.*

class DetalhesAtividade : AppCompatActivity() {

    lateinit var atividade: Atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_atividade)

        atividade = intent?.extras!!.get("ATIVIDADE") as Atividade

        //tipoAtividade.text = atividade.tipo[0].toTitleCase() + atividade.tipo.substring(1)
        tipoAtividade.text = when (atividade.tipo){
            "workshop" -> "Workshop"
            "palestra" -> "Palestra"
            "mesaRedonda" -> "Mesa Redonda"
            else -> ""
        }
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

    }
}

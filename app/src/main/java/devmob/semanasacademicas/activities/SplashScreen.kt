package devmob.semanasacademicas.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.weeks
import devmob.semanasacademicas.R
import devmob.semanasacademicas.names
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.alert

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weeks = mutableListOf<Evento>()

        FirebaseFirestore.getInstance().weeks.get().addOnCompleteListener {task->
            if (task.isSuccessful){
                for (document in task.result!!.documents){
                    val aux = document.toObject(Evento::class.java)
                    aux!!.id = document.id
                    weeks.add(aux)
                }
                Log.d("mydebug", "Eventos encontrados na splash screen ${weeks.names()}")
                startActivity<TelaPrincipal>("WEEKS" to weeks as ArrayList)
            }
            else {
                longToast("Algo deu errado")
                Log.d("mydebug", "Erro ao acessar semanas durante a splash screen")
                startActivity<TelaPrincipal>()
            }
            this.finish()
        }
    }
}
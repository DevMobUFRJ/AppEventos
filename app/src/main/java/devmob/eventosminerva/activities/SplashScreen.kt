package devmob.eventosminerva.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import devmob.eventosminerva.dataclass.Evento
import devmob.eventosminerva.weeks
import devmob.eventosminerva.names
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

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
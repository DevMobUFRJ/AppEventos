package devmob.semanasacademicas

import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.dataclass.Evento
import java.text.SimpleDateFormat
import java.util.*

fun Timestamp.formata() = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataDia() = SimpleDateFormat("dd MMMM',' EE ", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Timestamp.formataMes() = SimpleDateFormat("dd MMM", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Timestamp.formataBarra() = SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Timestamp.formataHora() = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataSemana() = SimpleDateFormat("dd EE", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun String.formataTipo() = when (this){
    "workshop" -> "Workshop"
    "palestra" -> "Palestra"
    "mesaRedonda" -> "Mesa Redonda"
    else -> ""
}

class Types{
    companion object {
        const val others = "outros"
        const val workshop = "workshop"
        const val mesaRedonda = "mesaRedonda"
        const val lecture = "palestra"
        const val pause = "intervalo"
        const val all = "todos"

    }
}

const val ARG_WEEK_ID = "week_id"
const val ARG_ATIVIDADE_ID = "atividade_id"
const val ARG_EVENT = "EVENTO"
const val ARG_TYPE = "TIPO"
const val ARG_LISTA_ATIVIDADES ="lista_de_atividades"
const val ARG_SECTION_NUMBER = "section_number"
const val ARG_ATIVIDADE = "atividade"
const val ACTIVITY_ID = "id"
const val WEEK_ID = "weekId"

val FirebaseFirestore.weeks: CollectionReference
    get() = this.collection("semanas")

val DocumentReference.activities: CollectionReference
    get() = this.collection("atividades")

val DocumentReference.shop: CollectionReference
    get() = this.collection("loja")

val FirebaseFirestore.users: CollectionReference
    get() = this.collection("users")

val DocumentReference.favorites: CollectionReference
    get() = this.collection("favoriteActivities")

operator fun CollectionReference.get(id: String) = this.document(id)

fun ProgressBar.show() {
    this.visibility = View.VISIBLE
}

fun ProgressBar.dismiss() {
    this.visibility = View.GONE
}

fun EditText.setErrorAndFocus(string: String){
    this.error = string
    this.requestFocus()
}
val Task<Any>.endListener: Boolean
    get() = false


fun ImageButton.isFavorited(bool: Boolean){
    if (bool){
        //this.isActivated = true
        this.isSelected = true
        this.setImageResource(R.drawable.check_icon)
    }
    else {
        //this.isActivated = false
        this.isSelected = false
        this.setImageResource(R.drawable.add_favorite)
    }
}



//usado para debug
fun MutableList<Evento>.names(): String{
    var st = ""
    for (week in this){
        st += week.nome + ", "
    }
    return st
}

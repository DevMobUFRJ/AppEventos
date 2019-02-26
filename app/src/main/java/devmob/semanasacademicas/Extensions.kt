package devmob.semanasacademicas

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun Timestamp.formata() = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataDia() = SimpleDateFormat("dd MMMM',' EE ", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Timestamp.formataHora() = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataSemana() = SimpleDateFormat("dd EE", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun String.formataTipo() = when (this){
    "workshop" -> "Workshop"
    "palestra" -> "Palestra"
    "mesaRedonda" -> "Mesa Redonda"
    else -> ""
}
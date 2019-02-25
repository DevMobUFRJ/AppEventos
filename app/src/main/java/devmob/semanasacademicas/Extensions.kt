package devmob.semanasacademicas

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun Timestamp.formata() = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataDia() = SimpleDateFormat("dd MMMM',' EE ", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

fun Timestamp.formataHora() = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(this.toDate())

fun Timestamp.formataSemana() = SimpleDateFormat("dd EE", Locale("pt", "BR")).format(this.toDate()).toUpperCase()

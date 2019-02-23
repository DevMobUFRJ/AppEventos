package devmob.semanasacademicas

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun Timestamp.format() = SimpleDateFormat("dd/MM", Locale("pt", "BR")).format(this.toDate())
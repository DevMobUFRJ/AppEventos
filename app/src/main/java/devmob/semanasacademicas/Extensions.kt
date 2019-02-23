package devmob.semanasacademicas

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun Timestamp.formata() = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR")).format(this.toDate())
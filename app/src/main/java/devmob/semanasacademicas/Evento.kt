package devmob.semanasacademicas

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Evento (
    var nome: String = "",
    var descricao: String = "",
    var link: String = "",
    var id: String = "",
    var inicio: Timestamp = Timestamp.now(),
    var fim: Timestamp = Timestamp.now()
):Parcelable {

    //fun periodo() = inicio.formata() + " ate " + fim.formata()
    fun periodo() = if (inicio.toDate().month == fim.toDate().month) "${inicio.toDate().date} a ${fim.formata()}" else "${inicio.formata()} a ${fim.formata()}"


    fun dias(): ClosedRange<Calendar> {
        val inicial = Calendar.getInstance()
        inicial.time = inicio.toDate()

        val final = Calendar.getInstance()
        final.time = fim.toDate()

        return inicial..final
    }

}

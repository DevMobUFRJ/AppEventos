package devmob.semanasacademicas.dataclass

import android.os.Parcelable
import com.google.firebase.Timestamp
import devmob.semanasacademicas.formata
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Evento (
    var nome: String = "",
    var descricao: String = "",
    var link: String = "",
    var id: String = "",
    var inicio: Timestamp = Timestamp.now(),
    var fim: Timestamp = Timestamp.now(),
    var listaTipos: List<String> = listOf()
):Parcelable {

    //fun periodo() = inicio.formata() + " ate " + fim.formata()
    fun periodo() = if (inicio.toDate().month == fim.toDate().month) "${inicio.toDate().date} a ${fim.formata()}" else "${inicio.formata()} a ${fim.formata()}"
}

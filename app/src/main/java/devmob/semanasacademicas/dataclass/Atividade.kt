package devmob.semanasacademicas.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Atividade (
    var grupo: String = "",
    var tipo: String = "outros",
    var apresentador: String = "Apresentador não informado",
    var local: String = "Local não informado",
    var weekId: String = ""
): Evento(), Parcelable

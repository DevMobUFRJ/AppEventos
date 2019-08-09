package devmob.eventosminerva.dataclass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemLoja(
    var nome: String = "",
    var descricao: String = "",
    var preco: Double = 0.0,
    var id: String = "",
    var link: String = ""
): Parcelable
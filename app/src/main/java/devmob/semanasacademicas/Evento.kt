package devmob.semanasacademicas

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Evento (
    var nome: String = "",
    var descricao: String = "",
    var link: String = "",
    var id: String = "",
    var inicio: Timestamp = Timestamp.now(),
    var fim: Timestamp = Timestamp.now()
):Parcelable {

    fun periodo() = inicio.toDate().toString()

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readParcelable(Timestamp::class.java.classLoader)
    )
    //    fun periodo()
//            = "De ${inicio.get(Calendar.DAY_OF_MONTH)}/${inicio.get(Calendar.MONTH)+1} " +
//            "ate ${fim.get(Calendar.DAY_OF_MONTH)}/${fim.get(Calendar.MONTH)+1}"
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nome)
        parcel.writeString(descricao)
        parcel.writeString(link)
        parcel.writeString(id)
        parcel.writeParcelable(inicio, flags)
        parcel.writeParcelable(fim, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Evento> {
        override fun createFromParcel(parcel: Parcel) = Evento(parcel)
        override fun newArray(size: Int): Array<Evento?> = arrayOfNulls(size)
    }
}

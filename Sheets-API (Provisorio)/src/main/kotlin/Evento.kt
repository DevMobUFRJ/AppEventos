import com.google.cloud.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class Evento (
    var nome: String = "",
    var tipo: String = "",
    var descricao: String = "",
    var link: String = "",
    var linkEvento: String = "",
    var inicio: Timestamp = Timestamp.now(),
    var fim: Timestamp = Timestamp.now(),
    var listaTipos: List<String> = listOf(),
    var color1: String = "#000000",
    var color2: String = "#D18C22",
    var linkInscricao: String = ""
) {
    companion object {
        fun fromRow(row: MutableList<Any>) = Evento().apply {
            val df = SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
            }

            tipo = row[1].strAndTrim()

            inicio = Timestamp.of(df.parse(row[2].strAndTrim() + " GMT-03:00"))
            fim = Timestamp.of(df.parse(row[3].strAndTrim() + " GMT-03:00"))

            nome = row[4].strAndTrim()
            descricao = row[5].strAndTrim()

            linkInscricao = row[6].strAndTrim()
            linkEvento = row[7].strAndTrim()

            link = row[8].strAndTrim()
        }
    }
}

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
            for((idx, cell) in row.withIndex()) if(cell.strAndTrim().isNotEmpty())
                when(idx){
                    1 -> tipo = cell.strAndTrim()
                    2 -> inicio = Timestamp.of(df.parse(cell.strAndTrim() + " GMT-03:00"))
                    3 -> fim = Timestamp.of(df.parse(cell.strAndTrim() + " GMT-03:00"))
                    4 -> nome = cell.strAndTrim()
                    5 -> descricao = cell.strAndTrim()
                    6 -> linkInscricao = cell.strAndTrim()
                    7 -> linkEvento = cell.strAndTrim()
                    8 -> link = cell.strAndTrim()
                }
        }
    }
}

import com.google.cloud.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class Atividade (
    var nome: String = "",
    var grupo: String = "",
    var tipo: String = "outros",
    var inicio: Timestamp = Timestamp.now(),
    var fim: Timestamp = Timestamp.now(),
    var apresentador: String = "Apresentador não informado",
    var local: String = "Local não informado",
    var link: String = "https://fb.com/SemanaDaComputacaoUFRJ",
    var temInscricao: Boolean = false,
    var linkPalestrante: String = ""
) {
    companion object{
        fun fromRow(row: MutableList<Any>) = Atividade().apply {
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm z").apply {
                timeZone = TimeZone.getTimeZone("GMT")
            }
            for((idx, cell) in row.withIndex()) if(cell.toString().isNotEmpty())
                when(idx){
                    1 -> nome = cell.strAndTrim()
                    2 -> apresentador = cell.strAndTrim()
                    3 -> grupo = cell.strAndTrim()
                    4 -> local = cell.strAndTrim()
                    5 -> {}
                    6 -> inicio = Timestamp.of(df.parse(row[5].strAndTrim() + " " + cell.strAndTrim() + " GMT-03:00"))
                    7 -> {}
                    8 -> {
                        fim =
                            if(row[7].toString().isEmpty())
                                Timestamp.of(df.parse(row[5].strAndTrim() + " " + cell.strAndTrim() + " GMT-03:00"))
                            else
                                Timestamp.of(df.parse(row[7].strAndTrim() + " " + cell.strAndTrim() + " GMT-03:00"))
                    }
                    9 -> temInscricao = cell.strAndTrim() == "s"
                    10 -> link = cell.strAndTrim()
                    11 -> tipo = cell.strAndTrim()
                    12 -> linkPalestrante = row[12].strAndTrim()
                }
        }
    }
}

fun Any.strAndTrim() = toString().trim()
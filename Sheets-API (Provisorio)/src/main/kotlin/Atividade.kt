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

            nome = row[1].toString()
            apresentador = row[2].toString()
            grupo = row[3].toString()
            local = row[4].toString()

            inicio = Timestamp.of(df.parse(row[5].toString() + " " + row[6].toString() + " GMT-03:00"))

            val temp = if(row[7].toString().isEmpty()) row[5].toString() else row[7].toString()
            fim = Timestamp.of(df.parse(temp + " " + row[8].toString() + " GMT-03:00"))

            temInscricao = row[9].toString() == "s"

            link = row[10].toString()
            tipo = row[11].toString()

            linkPalestrante = if(row.size > 12) row[12].toString() else ""
        }
    }
}

package devmob.semanasacademicas

import com.google.firebase.Timestamp

data class Atividade (
    var nome: String = "",
    var descricao: String = "",
    var tipo: String = "",
    var inicio: Timestamp = Timestamp.now(),
    var apresentador: String = "",
    var local: String = ""
)
fun main() {
    val gs = GoogleSheets()
    val db = Firestore()

    for((row, l) in gs.atividades.withIndex()) {
        val actId = db.sendAtividade(Atividade.fromRow(l), l[0].toString(), "XXrEGjscQHDCgUratFSy")
        gs.setCell(actId, row)
    }
}


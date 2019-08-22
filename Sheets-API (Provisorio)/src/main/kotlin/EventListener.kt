class EventListener(sheet_id: String ) {
    private val gs = GoogleSheets(sheet_id)
    private val db = Firestore()

    fun attAll() {
        val temp = gs.getEvent()[0]

        val evento = Evento.fromRow(temp)
        val activities = gs.getActivities().map { Atividade.fromRow(it) to it[0].toString().trim() }

        evento.listaTipos = activities.map { it.first.tipo }.distinct()

        val eventId = db.sendEvento(evento, temp[0].toString())
        gs.setCell(eventId, 0, GoogleSheets.EVENT_ID)

        for((idx, act) in activities.withIndex())
            gs.setCell(db.sendAtividade(act.first, eventId, act.second), idx, GoogleSheets.ACTIVITY_ID)
    }
}
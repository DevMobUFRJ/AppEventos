class EventListener(val eventName: String, sheet_id: String ) {
    var quit = false
    private val gs = GoogleSheets(sheet_id)
    private val db = Firestore()

    private var lastLocalUpdate = ""

    val needUpdate: Boolean
        get() = gs.getLastUpdate()[0][0].toString() != lastLocalUpdate

    fun attAll() {
        val temp = gs.getEvent()[0]
        val evento = Evento.fromRow(temp)
        print(".")

        val activities = gs.getActivities().map { Atividade.fromRow(it) to it[0].toString().trim() }
        evento.listaTipos = activities.map { it.first.tipo }.distinct()
        print(".")

        val eventId = db.sendEvento(evento, temp[0].toString())
        gs.setCell(listOf(eventId), GoogleSheets.EVENT_ID)
        print(".")

        gs.setCell(activities.map { db.sendAtividade(it.first, eventId, it.second) }, GoogleSheets.ACTIVITY_ID)
        print(".")

        lastLocalUpdate = gs.getLastUpdate()[0][0].toString()
        print(".")
    }
}
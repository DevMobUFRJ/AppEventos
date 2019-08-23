import kotlinx.coroutines.*

val events = mutableMapOf<String, EventListener>()

fun main() = runBlocking {
    Firestore.start()

    val central = GoogleSheets("1pMHykzAL1UZVpbhNWlo5xgPYvXaP8n7L4pXIB4a0xiY")
    while(true){
        central.getListOfEvents().filter {it.size == 2}.run {
            forEach { if(it[1] !in events)  launch { loadAndListenSheet(it[0].strAndTrim(), it[1].strAndTrim()) }}
            events.filter { it.key !in map { it[1] } }.values.forEach { it.quit = true }
        }
        delay(1000 * 60 * 60)
    }
}

suspend fun loadAndListenSheet(name: String, id: String){
    val list = EventListener(name, id)
    events[id] = list

    while (true){
        if(list.needUpdate){
            print("Updating: ${list.eventName}")
            list.attAll()
            println("done")
        }

        if(list.quit) break
        delay(1000 * 60)
    }
    events.remove(id)
    println(list.eventName + " quited")
}

fun Any.strAndTrim() = toString().trim()
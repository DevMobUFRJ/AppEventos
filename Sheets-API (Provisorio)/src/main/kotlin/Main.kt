import kotlinx.coroutines.*

val ids = listOf(
    "1uMZiPnAk8MpD6phijjFmmXkB1oVrcXXSxEHtzAN5TXk",
    "1fzTOF5AhdeydLNP6sTebvMhcp7uY3ceufdtyEcb7wlY")

fun main() = runBlocking {
    Firestore.start()
    for(id in ids) launch { loadAndListenSheet(id) }
}

fun loadAndListenSheet(id: String){
    val list = EventListener(id)
    list.attAll()
}

fun Any.strAndTrim() = toString().trim()
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileNotFoundException

class Firestore {
    companion object {
        private const val accountPath = "bd-firestore.json"

        private val serviceAccount = Firestore::class.java.getResourceAsStream(accountPath)
            ?: throw FileNotFoundException("Resource not found: $accountPath")

        lateinit var db: com.google.cloud.firestore.Firestore

        fun start() = with(GoogleCredentials.fromStream(serviceAccount)) {
            FirestoreOptions.newBuilder().setTimestampsInSnapshotsEnabled(true).setCredentials(this).build().also {
                db = it.service
            }
        }
    }

    fun sendAtividade(act: Atividade, weekId: String, actId: String = "") = with(db.collection("semanas").document(weekId).collection("atividades")){
        if(actId.isEmpty()) add(act).get().path.getActID()
        else {
            document(actId).set(act).get()
            actId
        }
    }

    fun sendEvento(evn: Evento, weekId: String = "") = with(db.collection("semanas")) {
        if(weekId.isEmpty()) add(evn).get().path.getEvnID()
        else {
            document(weekId).set(evn).get()
            weekId
        }
    }

    private fun String.getActID() = split("/")[3]
    private fun String.getEvnID() = split("/")[1]
}

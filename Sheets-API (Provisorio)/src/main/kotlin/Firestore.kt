import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import java.io.FileNotFoundException

class Firestore {
    companion object {
        const val accountPath = "bd-firestore.json"
    }

    private val db: com.google.cloud.firestore.Firestore

    init {
        val serviceAccount = Firestore::class.java.getResourceAsStream(accountPath)
            ?: throw FileNotFoundException("Resource not found: $accountPath")

        with(GoogleCredentials.fromStream(serviceAccount)){
            FirebaseOptions.Builder().setCredentials(this).build().also {
                FirebaseApp.initializeApp(it)
                db = FirestoreClient.getFirestore()
            }
        }
    }

    fun sendAtividade(act: Atividade, actId: String?, weekId: String) = with(db.collection("semanas").document(weekId).collection("atividades")){
        if(actId.isNullOrEmpty()) add(act).get().path.getActID()
        else {
            document(actId).set(act).get()
            actId
        }
    }

    private fun String.getActID() = split("/")[3]

}

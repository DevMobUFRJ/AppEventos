import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.cloud.Timestamp
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*



class GoogleSheets {

    companion object {
        const val ACTIVITIES = "A11:N"
        const val SHEET_ID = "1uMZiPnAk8MpD6phijjFmmXkB1oVrcXXSxEHtzAN5TXk"

        const val name = "Semanas UFRJ"
        const val credentials_path = "google-sheets.json"

        private val json = JacksonFactory.getDefaultInstance()
        private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS)
        private val http_transport = GoogleNetHttpTransport.newTrustedTransport()
    }

    private val credential: Credential

    lateinit var atividades: MutableList<MutableList<Any>>

    init {
        credential = getCredentials()
        updateActivities()
    }

    fun updateActivities() {
        atividades = getRange()
    }

    private fun cellId(actId: String) = ValueRange()
        .setValues(listOf(
            listOf(actId)
        ))

    fun setCell(id: String, row: Int) =
        Sheets.Builder(http_transport, json, credential).setApplicationName(name).build().spreadsheets().values()
            .update(SHEET_ID, "'Sheet1'!A${row + 11}", cellId(id)).setValueInputOption("RAW").execute()

    private fun getRange() = Sheets.Builder(http_transport, json, credential)
        .setApplicationName(name).build()
        .spreadsheets().values().get(SHEET_ID, GoogleSheets.ACTIVITIES)
        .execute().getValues()

    private fun getCredentials(): Credential {
        val input = GoogleSheets::class.java.getResourceAsStream(credentials_path)
            ?: throw FileNotFoundException("Resource not found: $credentials_path")

        return GoogleCredential.fromStream(input).createScoped(scopes)
    }

}
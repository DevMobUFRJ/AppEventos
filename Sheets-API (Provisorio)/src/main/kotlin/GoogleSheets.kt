import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.FileNotFoundException
import com.google.api.services.sheets.v4.model.ValueRange
import java.util.*

class GoogleSheets(private val sheet_id: String) {

    companion object {
        const val ACTIVITIES = "A11:N"
        const val EVENT = "A3:H3"

        const val EVENT_ID = 3
        const val ACTIVITY_ID = 11

        const val name = "Semanas UFRJ"
        const val credentials_path = "google-sheets.json"

        private val json = JacksonFactory.getDefaultInstance()
        private val scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS)
        private val http_transport = GoogleNetHttpTransport.newTrustedTransport()
    }

    private val credential = getCredentials()

    fun getEvent() = getRange(EVENT)
    fun getActivities() = getRange(ACTIVITIES)

    fun setCell(id: String, row: Int, type: Int) = Sheets.Builder(http_transport, json, credential)
        .setApplicationName(name).build()
        .spreadsheets().values()
        .update(sheet_id, "'Sheet1'!A${row + type}", cellId(id))
        .setValueInputOption("RAW").execute()

    private fun cellId(actId: String) = ValueRange()
        .setValues(listOf(
            listOf(actId)
        ))

    private fun getRange(range: String) = Sheets.Builder(http_transport, json, credential)
        .setApplicationName(name).build()
        .spreadsheets().values().get(sheet_id, range)
        .execute().getValues()

    private fun getCredentials(): Credential {
        val input = GoogleSheets::class.java.getResourceAsStream(credentials_path)
            ?: throw FileNotFoundException("Resource not found: $credentials_path")
        return GoogleCredential.fromStream(input).createScoped(scopes)
    }

}
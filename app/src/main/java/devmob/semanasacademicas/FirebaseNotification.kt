package devmob.semanasacademicas

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Color
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import devmob.semanasacademicas.activities.DetalhesAtividade
import devmob.semanasacademicas.dataclass.Atividade
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import java.util.*

const val CHANNEL_ID = "42"

class FirebaseNotification: FirebaseMessagingService() {

//    override fun onNewToken(token: String?) {
//        super.onNewToken(token)
//
//        FirebaseAuth.getInstance().uid?.also {
//            val campos = HashMap<String, String?>()
//            campos["token"] = token
//            FirebaseFirestore.getInstance().users[it].set(campos, SetOptions.merge())
//        }
//
//    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        val data = message?.data!!
        val atividade = Atividade()

        atividade.id = data["id"]!!
        atividade.nome = data["nome"]!!
        atividade.grupo = data["grupo"]!!
        atividade.tipo = data["tipo"]!!
        atividade.inicio = Timestamp(data["inicio"]!!.toLong(), 0)
        atividade.fim = Timestamp(data["fim"]!!.toLong(), 0)
        atividade.apresentador = data["apresentador"]!!
        atividade.local = data["local"]!!
        atividade.weekId = data["weekId"]!!

        val intent = intentFor<DetalhesAtividade>(
            ARG_ATIVIDADE to atividade
        ).clearTop()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            setupChannel()

        val notificationId = Random().nextInt(500000)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(data.get("title"))
            .setContentText(data.get("body"))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(api = android.os.Build.VERSION_CODES.O)
    fun setupChannel(){
        val adminChannelName = "APPEVENTOS_NOTIFICATION_CHANNEL"
        val adminChannelDescription = "Channel for appeventos"

        val adminChannel = NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.run {
            description = adminChannelDescription
            lightColor = Color.RED
            enableLights(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(adminChannel)
    }

}

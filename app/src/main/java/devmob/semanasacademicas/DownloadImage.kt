package devmob.semanasacademicas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.URL

class DownloadImage(val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
    override fun doInBackground(vararg params: String?) =
        try {
            val url = params[0]
            val iStream = URL(url).openStream()
            BitmapFactory.decodeStream(iStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    override fun onPostExecute(result: Bitmap?) {
        result?.let{
            imageView.setImageBitmap(it)
        }
    }

}
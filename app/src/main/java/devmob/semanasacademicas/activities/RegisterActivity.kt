package devmob.semanasacademicas.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.Window
import devmob.semanasacademicas.R
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Cadastro"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {

        startActivity<LoginActivity>()
        overridePendingTransition(R.anim.fade_in, R.anim.exit_to_left)
    }

}

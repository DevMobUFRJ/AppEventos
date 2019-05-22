package devmob.semanasacademicas.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import devmob.semanasacademicas.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtRegister.setOnClickListener {
            startActivity<RegisterActivity>()
            overridePendingTransition(R.anim.enter_from_left, R.anim.fade_out)
        }
    }
}

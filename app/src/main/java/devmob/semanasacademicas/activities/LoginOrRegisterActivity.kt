package devmob.semanasacademicas.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.*
import devmob.semanasacademicas.R
import devmob.semanasacademicas.fragments.RegisterFragment
import devmob.semanasacademicas.fragments.LoginFragment
import kotlinx.android.synthetic.main.activity_login_or_register.*
import org.jetbrains.anko.startActivity

class LoginOrRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_or_register)
        setSupportActionBar(toolbar)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentLogin, LoginFragment())
            .commit()


        /*
        val pagerAdapter = SlideAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter
        */
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.contentLogin) is RegisterFragment){
            supportFragmentManager.beginTransaction()
                //.setCustomAnimations()
                .replace(R.id.contentLogin, LoginFragment()).commit()
        }
        else{
            //startActivity<TelaPrincipal>()
            super.onBackPressed()
        }
    }

    /*
    override fun onBackPressed() =
        if(mPager.currentItem == 0) super.onBackPressed()
        else mPager.currentItem = 0

    private inner class SlideAdapter(fm : FragmentManager): FragmentPagerAdapter(fm) {
        override fun getCount() = 2

        override fun getItem(p0: Int) = if(p0 == 0) LoginFragment() else RegisterFragment()
    }
    */
}

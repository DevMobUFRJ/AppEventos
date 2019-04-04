package devmob.semanasacademicas.activities

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.R
import devmob.semanasacademicas.R.id.nav_login
import devmob.semanasacademicas.R.id.nav_logout
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentMinhaSemana
import devmob.semanasacademicas.fragments.FragmentTelaPrincipal
import devmob.semanasacademicas.fragments.LoginFragment
import devmob.semanasacademicas.names
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.view.*
import org.jetbrains.anko.startActivity

class TelaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*
        nav_view.setCheckedItem(R.id.nav_eventos)
        displayScreen(R.id.nav_eventos)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null){
            //nav_view.getHeaderView(0).visibility = View.GONE
            nav_view.menu.findItem(R.id.nav_login).isVisible = true
            nav_view.menu.findItem(R.id.nav_minha_conta).isVisible = false
            nav_view.menu.findItem(R.id.nav_agenda).isVisible = false
            nav_view.menu.findItem(R.id.nav_historico).isVisible = false
            nav_view.menu.findItem(R.id.nav_sorteio).isVisible = false
            nav_view.menu.findItem(R.id.nav_logout).isVisible = false

        }
        else {
            nav_view.getHeaderView(0).run {
                email.text = user.email!!
                nome.text = user.displayName!!
            }
            nav_view.menu.findItem(R.id.nav_login).isVisible = false
            nav_view.menu.findItem(R.id.nav_minha_conta).isVisible = true
            nav_view.menu.findItem(R.id.nav_agenda).isVisible = true
            nav_view.menu.findItem(R.id.nav_historico).isVisible = true
            nav_view.menu.findItem(R.id.nav_sorteio).isVisible = true
            nav_view.menu.findItem(R.id.nav_logout).isVisible = true
            nav_view.menu.findItem(R.id.nav_logout).icon.setColorFilter(resources.getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP)
        }*/


    }

    override fun onResume() {
        //val user = FirebaseAuth.getInstance().currentUser
        FirebaseAuth.getInstance().addAuthStateListener {
            val user = it.currentUser
            if (user == null){
                nav_view.getHeaderView(0).run {
                    email.text = resources.getText(R.string.nav_header_subtitle)
                    nome.text = resources.getText(R.string.nav_header_title)
                }
                nav_view.menu.findItem(R.id.nav_login).isVisible = true
                nav_view.menu.findItem(R.id.nav_minha_conta).isVisible = false
                nav_view.menu.findItem(R.id.nav_agenda).isVisible = false
                nav_view.menu.findItem(R.id.nav_historico).isVisible = false
                nav_view.menu.findItem(R.id.nav_sorteio).isVisible = false
                nav_view.menu.findItem(R.id.nav_logout).isVisible = false

            }
            else {
                nav_view.getHeaderView(0).run {
                    email.text = user!!.email!!
                    nome.text = user!!.displayName!!
                }
                nav_view.menu.findItem(R.id.nav_login).isVisible = false
                nav_view.menu.findItem(R.id.nav_minha_conta).isVisible = true
                nav_view.menu.findItem(R.id.nav_agenda).isVisible = true
                nav_view.menu.findItem(R.id.nav_historico).isVisible = true
                nav_view.menu.findItem(R.id.nav_sorteio).isVisible = true
                nav_view.menu.findItem(R.id.nav_logout).isVisible = true
                nav_view.menu.findItem(R.id.nav_logout).icon.setColorFilter(resources.getColor(R.color.Red), PorterDuff.Mode.SRC_ATOP)
            }
            nav_view.setCheckedItem(R.id.nav_eventos)
            displayScreen(R.id.nav_eventos)

        }
        super.onResume()
    }

    private fun displayScreen(id: Int){
        var fragment: Fragment = FragmentTelaPrincipal()
        when (id) {
            R.id.nav_eventos -> { //envia as semanas como parametro
                fragment = FragmentTelaPrincipal()
                val weeks = intent.extras?.getParcelableArrayList<Evento>("WEEKS")
                val bundle = Bundle()
                Log.d("mydebug", "Eventos passados pela activity principal (nav_eventos) ${weeks?.names()}")
                bundle.putParcelableArrayList("WEEKS", weeks)
                fragment.arguments = bundle
            }
            R.id.nav_agenda -> {
                fragment = FragmentMinhaSemana()
            }/*
            R.id.nav_login -> {
                finish()
                startActivity<LoginOrRegisterActivity>()
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
            }*/
            else -> {
                fragment = FragmentTelaPrincipal()
                val weeks = intent.extras?.getParcelableArrayList<Evento>("WEEKS")
                val bundle = Bundle()
                Log.d("mydebug", "Eventos passados pela activity principal (else) ${weeks?.names()}")
                bundle.putParcelableArrayList("WEEKS", weeks)
                fragment.arguments = bundle
            }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentHome, fragment)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tela_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            nav_login -> startActivity<LoginOrRegisterActivity>()
            nav_logout -> FirebaseAuth.getInstance().signOut()
            else -> displayScreen(item.itemId)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
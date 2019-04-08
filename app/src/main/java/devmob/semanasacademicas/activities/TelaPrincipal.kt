package devmob.semanasacademicas.activities

import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.R
import devmob.semanasacademicas.R.id.nav_login
import devmob.semanasacademicas.R.id.nav_logout
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.fragments.FragmentMinhaSemana
import devmob.semanasacademicas.fragments.FragmentTelaDeEvento
import devmob.semanasacademicas.fragments.FragmentTelaPrincipal
import devmob.semanasacademicas.fragments.SettingsFragment
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity

class TelaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var model: WeeksList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        model = ViewModelProviders.of(this).get(WeeksList::class.java)

        intent.extras?.getParcelableArrayList<Evento>("WEEKS")?.also {
            model.setWeeks(it)
        }

        model.screen = model.screen ?: R.id.nav_eventos

        nav_view.setCheckedItem(model.screen!!)
        displayScreen(model.screen!!)

        FirebaseAuth.getInstance().addAuthStateListener {
            val user = it.currentUser

            nav_view.getHeaderView(0).run {
                email.text = user?.email ?: resources.getText(R.string.nav_header_subtitle)
                nome.text = user?.displayName ?: resources.getText(R.string.nav_header_title)
            }

            var userIsNull = true
            user?.also {
                userIsNull = false
                nav_view.menu.findItem(R.id.nav_logout).icon
                    .setColorFilter(ContextCompat.getColor(this, R.color.Red), PorterDuff.Mode.SRC_ATOP)
            }

            nav_view.menu.run {
                findItem(R.id.nav_login).isVisible = userIsNull
                findItem(R.id.nav_minha_conta).isVisible = !userIsNull
                findItem(R.id.nav_agenda).isVisible = !userIsNull
                findItem(R.id.nav_historico).isVisible = !userIsNull
                findItem(R.id.nav_sorteio).isVisible = !userIsNull
                findItem(R.id.nav_logout).isVisible = !userIsNull
            }
        }
    }

    fun displayScreen(id: Int) {
        val transaction = supportFragmentManager.beginTransaction()

        when (id) {
            R.id.nav_eventos -> replace(FragmentTelaPrincipal(), transaction)
            R.id.nav_agenda -> replace(FragmentMinhaSemana(), transaction)
            R.id.nav_config -> replace(SettingsFragment(), transaction)
            20 -> replace(FragmentTelaDeEvento(), transaction)
            else -> replace(FragmentTelaPrincipal(), transaction)
        }

        transaction.commit()
        model.screen = id
    }

    private fun replace(frag: Fragment, transaction: FragmentTransaction){
        transaction.replace(R.id.contentHome, frag, frag.javaClass.name)
        if(supportFragmentManager.findFragmentByTag(FragmentTelaPrincipal().javaClass.name) == null) transaction.addToBackStack(frag.javaClass.name)
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStackImmediate()
            else -> super.onBackPressed()
        }

        when(model.screen){
            R.id.nav_agenda -> {
                model.screen = R.id.nav_eventos
                nav_view.setCheckedItem(model.screen!!)
            }
            20 -> model.screen = R.id.nav_eventos
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tela_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId){
        R.id.action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            nav_login -> {
                startActivity<LoginOrRegisterActivity>()
                return false
            }
            nav_logout -> {
                alert("Deseja sair?") {
                    negativeButton("Não") {  }
                    positiveButton("Sim") { FirebaseAuth.getInstance().signOut() }
                }.show()
                return false
            }
            else -> displayScreen(item.itemId)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
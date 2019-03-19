package devmob.semanasacademicas

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.content_tela_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.view.*
import org.jetbrains.anko.toast
import java.util.*

class TelaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //troca o layout da tela home pelo layout do item selecionado
    fun displayScreen(id: Int){
        val fragment = when (id) {
            R.id.nav_eventos -> FragmentTelaPrincipal()
            R.id.nav_agenda -> FragmentMinhaSemana()
            else -> FragmentTelaPrincipal()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentHome, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_principal)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_eventos)
        displayScreen(R.id.nav_eventos)

        nav_view.getHeaderView(0).setOnClickListener {
            toast("Clicou")
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        displayScreen(item.itemId)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
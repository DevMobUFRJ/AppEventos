package devmob.semanasacademicas.activities

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.SearchView
import androidx.transition.Scene
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.R
import devmob.semanasacademicas.R.id.*
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.User
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.activity_tela_principal.*
import kotlinx.android.synthetic.main.app_bar_tela_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import androidx.core.view.MenuItemCompat.collapseActionView
import androidx.databinding.adapters.SearchViewBindingAdapter.setOnQueryTextListener
import android.util.Log
import devmob.semanasacademicas.fragments.*


class TelaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var user: User
    lateinit var model: WeeksList
    var showSearchButton = false

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
        user = ViewModelProviders.of(this).get(User::class.java)

        intent.extras?.getParcelableArrayList<Evento>("WEEKS")?.also {
            model.setWeeks(it)
        }

        model.screen = model.screen ?: nav_eventos

        nav_view.setCheckedItem(model.screen!!)
        displayScreen(model.screen!!)
        toolbar.title = "Eventos"

        FirebaseAuth.getInstance().addAuthStateListener {
            val user = it.currentUser

            nav_view.getHeaderView(0).run {
                email.text = user?.email ?: resources.getText(R.string.nav_header_subtitle)
                nome.text = user?.displayName ?: resources.getText(R.string.nav_header_title)
            }

            var userIsNull = true
            user?.also {
                userIsNull = false
                nav_view.menu.findItem(nav_logout).icon
                    .setColorFilter(ContextCompat.getColor(this, R.color.Red), PorterDuff.Mode.SRC_ATOP)
            }

            nav_view.menu.run {
                findItem(nav_login).isVisible = userIsNull
                findItem(nav_minha_conta).isVisible = !userIsNull
                findItem(nav_agenda).isVisible = !userIsNull
//                findItem(nav_historico).isVisible = !userIsNull
                findItem(nav_historico).isVisible = false
//                findItem(nav_sorteio).isVisible = !userIsNull
                findItem(nav_sorteio).isVisible = false
                findItem(nav_logout).isVisible = !userIsNull
            }
        }
    }

    fun displayScreen(id: Int) {
        nav_view.setCheckedItem(id)
        model.screen = id

        when (id) {
            nav_eventos -> {
                //showSearchButton = true
                //invalidateOptionsMenu()
                //toolbar.title = "Eventos"
                replace(TabsFragment())
            }
            nav_agenda -> {
                //showSearchButton = false
                //invalidateOptionsMenu()
                //toolbar.title = "Minha semana"
                replace(FragmentMinhaSemana())
            }
            nav_config -> {
                //showSearchButton = false
                //invalidateOptionsMenu()
                //toolbar.title = "Configurações"
                replace(SettingsFragment())
            }
            //TODO: Criar um id pra tela de evento
            20 -> {
                //showSearchButton = false
                //invalidateOptionsMenu()
                replace(FragmentTelaDeEvento())
            }
            else -> {
                showSearchButton = true
                invalidateOptionsMenu()
                replace(FragmentTelaPrincipal())
            }
        }
    }

    private fun replace(frag: androidx.fragment.app.Fragment){
        toolbar.setBackgroundColor(Color.parseColor("#000000"))

        val transaction = supportFragmentManager.beginTransaction()
        val oldFrag = supportFragmentManager.findFragmentByTag(frag.javaClass.name)

        if(oldFrag != null) supportFragmentManager.popBackStack(oldFrag.javaClass.name, POP_BACK_STACK_INCLUSIVE)

        transaction.replace(R.id.contentHome, frag, frag.javaClass.name).addToBackStack(frag.javaClass.name)
        transaction.commit()
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            supportFragmentManager.backStackEntryCount > 1 -> {
                supportFragmentManager.popBackStack()
                model.screen = nav_eventos
                displayScreen(model.screen!!)
                //nav_view.setCheckedItem(model.screen!!)
            }
            supportFragmentManager.backStackEntryCount == 1 -> finish()
//            model.screen == R.id.nav_eventos -> finish()
//            model.screen == R.id.nav_agenda -> displayScreen(R.id.nav_eventos)
//            model.screen == 20 -> displayScreen(R.id.nav_eventos)
            else -> super.onBackPressed()
        }

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tela_principal, menu)


        val actionSearch = menu!!.findItem(R.id.action_search)
        actionSearch.isVisible = showSearchButton
        val searchView = actionSearch.actionView as SearchView //pega o botao de pesquisar

        //listener de texto alterado
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("mydebug", "SearchOnQueryTextSubmit: $query")
                return false
            }

            override fun onQueryTextChange(string: String): Boolean {
                Log.d("mydebug", "SearchOnQueryTextChanged: " + string)
                model.setWeeks(model.copy.filter { it.nome.contains(string, ignoreCase = true) } as MutableList<Evento>)
                return false
            }
        })

        //listener de fechar busca
        actionSearch.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                Log.d("mydebug", "Collapse")
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                Log.d("mydebug", "Expand")
                return true
            }
        })


        return super.onCreateOptionsMenu(menu)
    }




    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId){
        R.id.action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            nav_login -> {
                startActivity<LoginActivity>()
                drawer_layout.closeDrawer(GravityCompat.START)
                return false
            }
            nav_logout -> {
                alert("Deseja sair?") {
                    negativeButton("Não") {  }
                    positiveButton("Sim") {
                        FirebaseAuth.getInstance().signOut()
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }.show()
                return false
            }
            nav_ajuda -> {
                toast("Ainda não implementado")
                return false
            }
//            nav_config -> {
//                val sharedPref = getPreferences(Context.MODE_PRIVATE)
//                val total = sharedPref.getInt("alex", 0)
//
//                with(sharedPref.edit()){
//                    putInt("alex", total+1)
//                    apply()
//                }
//                toast("Ainda não implementado $total")
//                return false
//            }
            nav_historico -> {
                toast("Ainda não implementado")
                return false
            }
            nav_minha_conta -> {
                toast("Ainda não implementado")
                return false
            }
            else -> displayScreen(item.itemId)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
package devmob.semanasacademicas.activities

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import devmob.semanasacademicas.R
import devmob.semanasacademicas.R.id.*
import devmob.semanasacademicas.viewModels.SelectedWeek
import devmob.semanasacademicas.viewModels.User
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.nav_header_tela_principal.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast


class TelaPrincipal : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var weeksList: WeeksList
    lateinit var activitiesList: SelectedWeek
    lateinit var user: User

    lateinit var navController: NavController
    lateinit var searchButton: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, drawer_layout)
        nav_view.setupWithNavController(navController)
        nav_view.setNavigationItemSelectedListener(this)

        user = ViewModelProviders.of(this).get(User::class.java)
        weeksList = ViewModelProviders.of(this).get(WeeksList::class.java)
        activitiesList = ViewModelProviders.of(this).get(SelectedWeek::class.java)

        ViewModelProviders.of(this).get(User::class.java)

        weeksList.screen = weeksList.screen ?: nav_eventos
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

    fun addDestinationListener(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            appBar.setExpanded(true, true)
            when (destination.id) {
                nav_eventos -> {
                    searchButton.isVisible = true
                }
                fragmentTelaDeEvento -> {
                    supportActionBar?.title = activitiesList.selectedWeek.nome
                    searchButton.isVisible = false
                }
                fragmentDetalhesAtividade -> {
                    supportActionBar?.title = activitiesList.selectedWeek.nome
                }
                fragmentAtividades -> {
                    supportActionBar?.title = activitiesList.selectedWeek.nome
                }
                else -> {
                    searchButton.isVisible = false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(drawer_layout)
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tela_principal, menu)

        searchButton = menu!!.findItem(action_search)
        val searchView = searchButton.actionView as SearchView //pega o botao de pesquisar

        addDestinationListener()
        //listener de texto alterado
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("mydebug", "SearchOnQueryTextSubmit: $query")
                return false
            }

            override fun onQueryTextChange(string: String): Boolean {
                Log.d("mydebug", "SearchOnQueryTextChanged: " + string)
                weeksList.query = string
                weeksList.makeQuery()
//                weeksList.setWeeks(weeksList.copy.filter { it.nome.contains(string, ignoreCase = true) } as MutableList<Evento>)
                return false
            }
        })

//        //listener de fechar busca
//        actionSearch.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
//            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
//                Log.d("mydebug", "Collapse")
//                return true
//            }
//
//            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
//                Log.d("mydebug", "Expand")
//                return true
//            }
//        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item!!.itemId){
        action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        return when (item.itemId){
            nav_logout -> {
                alert("Deseja sair?") {
                    negativeButton("Não") { }
                    positiveButton("Sim") { FirebaseAuth.getInstance().signOut() }
                }.show()
                false
            }
            nav_ajuda -> {
                toast("Ainda não implementado")
                false
            }
            nav_historico -> {
                toast("Ainda não implementado")
                false
            }
            nav_minha_conta -> {
                toast("Ainda não implementado")
                false
            }
            else -> NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
        }
    }

}
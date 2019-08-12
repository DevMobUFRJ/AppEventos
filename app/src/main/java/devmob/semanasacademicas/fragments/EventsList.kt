package devmob.semanasacademicas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import devmob.semanasacademicas.R
import devmob.semanasacademicas.adapters.ListaDeEventosAdapter
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.viewModels.User
import devmob.semanasacademicas.viewModels.WeeksList
import kotlinx.android.synthetic.main.content_tela_principal.*
import org.jetbrains.anko.support.v4.onRefresh

class EventsList(val opt: Int) : Fragment() {

    lateinit var viewAdapter: ListaDeEventosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.content_tela_principal, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val model = ViewModelProviders.of(this.activity!!).get(WeeksList::class.java)
        val userFavorite = ViewModelProviders.of(this.activity!!).get(User::class.java)

        val viewManager = LinearLayoutManager(this.context)
        viewAdapter = ListaDeEventosAdapter()

        lista.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        swiperefresh.isRefreshing = true

        swiperefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        swiperefresh.onRefresh {
            swiperefresh.isRefreshing = true
            model.loadWeeks()
        }

        if (opt == 1) userFavorite.changed.observe(this, Observer {
            swiperefresh.isRefreshing = true
            model.loadWeeks()
        })

        model.weeks.observe(this, Observer<MutableList<Evento>>{ weekList ->
            viewAdapter.eventos = if(opt == 0) weekList!! else weekList.filter { it.id in userFavorite.favoriteEvents } as MutableList
            swiperefresh.isRefreshing = false
        })
    }
}

package devmob.eventosminerva.activities

import android.graphics.Color
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity

import androidx.fragment.app.FragmentManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.*
import devmob.eventosminerva.*
import devmob.eventosminerva.R
import devmob.eventosminerva.adapters.AtividadesListAdapter
import devmob.eventosminerva.dataclass.Atividade
import devmob.eventosminerva.dataclass.Evento

import kotlinx.android.synthetic.main.activity_atividades.*
import kotlinx.android.synthetic.main.fragment_atividades.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.okButton
import java.util.*
import kotlin.collections.HashMap

class AtividadesActivity : AppCompatActivity() {

    val atividades = HashMap<String, ArrayList<Atividade>>()

    private lateinit var listener: ListenerRegistration

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var evento: Evento

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atividades)

        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        val extras = intent?.extras!!

        evento = extras[ARG_EVENT] as Evento
        val tipo = extras[ARG_TYPE] as String

        toolbar.backgroundColor = Color.parseColor(evento.color1)
        appbar.backgroundColor = Color.parseColor(evento.color1)
        tabs.setSelectedTabIndicatorColor(Color.parseColor(evento.color2))

        Log.d("mydebug", tipo)

        val referencia = FirebaseFirestore.getInstance().weeks[evento.id].activities

        listener = when(tipo) {
            Types.all -> referencia
            else -> referencia.whereEqualTo("tipo", tipo)
        }.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            if(firebaseFirestoreException != null){
                alert(firebaseFirestoreException.message.toString(), "Opa, algo de errado aconteceu"){
                    okButton {}
                }.show()
                return@addSnapshotListener
            }

            for(change in querySnapshot!!.documentChanges){

                val temp = change.document.toObject(Atividade::class.java)
                temp.id = change.document.id
                temp.weekId = evento.id

                when(change.type){
                    DocumentChange.Type.ADDED ->
                        temp.inicio.formataSemana().also {
                            if(it !in atividades) atividades[it] = ArrayList()
                            atividades[it]!!.add(temp)
                        }

                    DocumentChange.Type.MODIFIED ->
                        temp.inicio.formataSemana().also {
                            for(i in 0 until (atividades[it]?.size ?: 0))
                                if(atividades[it]!![i].id == temp.id)
                                    atividades[it]!![i] = temp
                        }

                    DocumentChange.Type.REMOVED ->
                        for(entry in atividades){
                            val lista = entry.component2()
                            val busca = lista.find { it.id == temp.id }
                            if( busca != null) lista.remove(busca)
                        }
                }
            }
            attTabs()
        }

    }

    private fun attTabs() {
        val dias = atividades.keys.sorted()
        mSectionsPagerAdapter.run{
            for(dia in dias)
                if(dia !in tabItems) tabs.addTab(tabs.newTab().setText(dia))
            tabItems = dias
            notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_atividades, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.action_settings) true
        else super.onOptionsItemSelected(item)

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

        var tabItems = emptyList<String>()

        override fun getItem(position: Int)
                = PlaceholderFragment.newInstance(position, atividades[tabItems[position]]!!, evento.color1, evento.color2)

        override fun getCount() = tabItems.size

        override fun getItemPosition(obj: Any) = POSITION_NONE

    }

    class PlaceholderFragment : androidx.fragment.app.Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

            val rootView = inflater.inflate(R.layout.fragment_atividades, container, false)

            val atividades = arguments!!.getParcelableArrayList<Atividade>(ARG_LISTA_ATIVIDADES)
            val atividadesOrdenadas = atividades!!.sortedWith(compareBy { it.inicio })

            val color1 = arguments!!.getString(ARG_PRIMARY_COLOR)
            val color2 = arguments!!.getString(ARG_SECUNDARY_COLOR)

            val viewAdapter = AtividadesListAdapter(atividadesOrdenadas, color1!!, color2!!)
            val viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

            rootView.listaAtividades.apply {
                setHasFixedSize(false)
                layoutManager = viewManager
                adapter= viewAdapter
            }

            return rootView
        }

        companion object {
            fun newInstance(
                sectionNumber: Int,
                atividadesDoDia: ArrayList<Atividade>,
                color1: String,
                color2: String
            ): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putString(ARG_PRIMARY_COLOR, color1)
                args.putString(ARG_SECUNDARY_COLOR, color2)
                args.putParcelableArrayList(ARG_LISTA_ATIVIDADES, atividadesDoDia)
                fragment.arguments = args
                return fragment
            }
        }
    }

}

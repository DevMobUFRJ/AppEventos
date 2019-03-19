package devmob.semanasacademicas

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.google.firebase.firestore.*

import kotlinx.android.synthetic.main.activity_atividades.*
import kotlinx.android.synthetic.main.fragment_atividades.*
import kotlinx.android.synthetic.main.fragment_atividades.view.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Stream
import kotlin.collections.HashMap

class AtividadesActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */

    lateinit var evento: Evento
    val atividades = HashMap<String, ArrayList<Atividade>>()

    private lateinit var listener: ListenerRegistration

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    val db = FirebaseFirestore.getInstance()

    private fun addTab() {
        val dias = atividades.keys.sorted()
        dias.let {
            for(dia in it) if(dia !in mSectionsPagerAdapter!!.tabItems) tabs.addTab(tabs.newTab().setText(dia))
            mSectionsPagerAdapter!!.tabItems = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atividades)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        evento = intent?.extras!!.get("EVENTO") as Evento
        val tipo = intent?.extras!!.get("TIPO") as String //pega quais atividades deve mostrar na pagina

        val referencia = db.collection("semanas").document(evento.id).collection("atividades")

        listener = when(tipo) {
            "workshop", "palestra" -> referencia.whereEqualTo("tipo", tipo)
            else -> referencia
        }.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                Log.w("aaaaa", "Listen error", firebaseFirestoreException)
                return@addSnapshotListener
            }
            for(change in querySnapshot!!.documentChanges){

                val temp = change.document.toObject(Atividade::class.java)
                temp.id = change.document.id

                when(change.type){
                    DocumentChange.Type.ADDED -> {
                        Log.w("aaaaa", "add" + change.document.toString())

                        temp.inicio.formataSemana().also {
                            if(it !in atividades) atividades[it] = ArrayList()
                            atividades[it]!!.add(temp)
                        }


                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.w("aaaaa", "mod" + change.document.toString())

                        temp.inicio.formataSemana().also {
                            for(i in 0 until (atividades[it]?.size ?: 0))
                                if(atividades[it]!![i].id == temp.id)
                                    atividades[it]!![i] = temp
                        }

                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.w("aaaaa", "rem" + change.document.toString())

                        temp.inicio.formataSemana().also {
                            var idx: Int? = null

                            for(i in 0 until (atividades[it]?.size ?: 0))
                                if(atividades[it]!![i].id == temp.id)
                                    idx = i

                            atividades[it]!!.removeAt(idx!!)
                        }
                    }
                }
            }
            addTab()
            mSectionsPagerAdapter!!.notifyDataSetChanged()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_atividades, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var tabItems = emptyList<String>()

        override fun getItem(position: Int) = PlaceholderFragment.newInstance(position, atividades[tabItems[position]]!!)

        override fun getCount() = tabItems.size

        override fun getItemPosition(obj: Any) = POSITION_NONE

    }

    /**
     * A placeholder fragment containing a simple view.
     */

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView = inflater.inflate(R.layout.fragment_atividades, container, false)

            val atividades = arguments!!.getParcelableArrayList<Atividade>(ARG_LISTA_ATIVIDADES)
            val atividadesOrdenadas = atividades!!.sortedWith(compareBy { it.inicio })

            val viewAdapter = AtividadesListAdapter(atividadesOrdenadas)
            val viewManager = LinearLayoutManager(this.context)

            rootView.listaAtividades.apply {
                setHasFixedSize(false)
                layoutManager = viewManager
                adapter= viewAdapter
            }



            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"
            private const val ARG_LISTA_ATIVIDADES ="lista_de_atividades"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, atividadesDoDia: ArrayList<Atividade>): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                args.putParcelableArrayList(ARG_LISTA_ATIVIDADES, atividadesDoDia)
                fragment.arguments = args
                return fragment
            }
        }
    }

}

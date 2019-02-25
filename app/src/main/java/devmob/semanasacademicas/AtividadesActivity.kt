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
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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

    private fun addTab() {
        val dias = atividades.keys.sorted()
        dias.let {
            for(dia in dias) tabs.addTab(tabs.newTab().setText(dia))
            mSectionsPagerAdapter!!.tabItems = it
        }
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    val db = FirebaseFirestore.getInstance()

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

        when(tipo){
            "workshop", "palestra"  -> referencia.whereEqualTo("tipo", tipo)
            else-> referencia
        }.get()
            .addOnSuccessListener { result ->
                Log.e("sei", "la")
                for (document in result) {
                    val temp = document.toObject(Atividade::class.java)
                    temp.id = document.id
                    temp.inicio.formataSemana().let {
                        if(it !in atividades) atividades[it] = ArrayList()
                        atividades[it]!!.add(temp)
                    }
                }
                addTab()
                mSectionsPagerAdapter!!.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("alexlindo", "Error getting documents.", exception)
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
            val atividadesPorHora = HashMap<String, ArrayList<Atividade>>()

            for(atividade in atividades){
                atividade.inicio.formataHora().let{
                    if(it !in atividadesPorHora) atividadesPorHora[it] = ArrayList()
                    atividadesPorHora[it]!!.add(atividade)
                }
            }
            val listaDeHorarios = atividadesPorHora.keys.toList().sorted()
            rootView.listaAtividades.let {
                it.setAdapter(AtividadesListAdapter(this.context!!, listaDeHorarios, atividadesPorHora))
                for(i in 0..(atividadesPorHora.size-1)) it.expandGroup(i)
                it.setOnGroupClickListener { _, _, groupPosition, _ ->
                    it.expandGroup(groupPosition)
                    true
                }
                it.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                    val atividade = atividadesPorHora[listaDeHorarios[groupPosition]]!![childPosition]
                    Log.e("alexlindo", atividade.toString())
                    val intent = Intent(it.context, DetalhesAtividade::class.java)
                    intent.putExtra("ATIVIDADE",atividade)
                    it.context.startActivity(intent)
                    true
                }
            }


            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            private val ARG_LISTA_ATIVIDADES ="lista_de_atividades"

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

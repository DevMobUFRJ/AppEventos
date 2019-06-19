package devmob.semanasacademicas.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.Evento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import org.jetbrains.anko.okButton
import org.jetbrains.anko.support.v4.alert
import java.util.ArrayList

class SelectedWeek: ViewModel(){

    var atividade = Atividade()
    var selectedWeek = Evento()
    set(value) {
        if (value.id == field.id) {
            return
        } else if(true){
            if(::listener.isInitialized){
                listener.remove()
                atividades.clear()
            }
            listener = createListener(value.id)
            field = value
        }
    }
    lateinit var listener: ListenerRegistration

    val atividades = HashMap<String, ArrayList<Atividade>>()
    val hasChanges = MutableLiveData<Boolean>()

    var filtered = HashMap<String, ArrayList<Atividade>>()

    var tipo = Types.all
    set(value) {
        filterBy(value)
        field = value
    }

    init{
        hasChanges.postValue(false)
    }

    private fun createListener(weekId: String) = FirebaseFirestore.getInstance().weeks[weekId].activities
        .addSnapshotListener { querySnapshot, _ ->
            for (change in querySnapshot!!.documentChanges) {

                val temp = change.document.toObject(Atividade::class.java)
                temp.id = change.document.id
                temp.weekId = selectedWeek.id

                when (change.type) {
                    DocumentChange.Type.ADDED ->
                        temp.inicio.formataSemana().also {
                            if (it !in atividades) atividades[it] = ArrayList()
                            atividades[it]!!.add(temp)
                        }

                    DocumentChange.Type.MODIFIED ->
                        temp.inicio.formataSemana().also {
                            for (i in 0 until (atividades[it]?.size ?: 0))
                                if (atividades[it]!![i].id == temp.id)
                                    atividades[it]!![i] = temp
                        }

                    DocumentChange.Type.REMOVED ->
                        for (entry in atividades) {
                            val lista = entry.component2()
                            val busca = lista.find { it.id == temp.id }
                            if (busca != null) lista.remove(busca)
                        }
                }
            }
            filterBy(tipo)
        }

    fun filterBy(tipo: String) {
            filtered = when (tipo) {
                Types.all -> atividades
                else ->
                    hashMapOf<String, ArrayList<Atividade>>().apply {
                        atividades.forEach {
                            var temp = arrayListOf<Atividade>()
                            for (atividade in it.value) if (atividade.tipo == tipo) temp.add(atividade)
                            if (temp.size > 0) this[it.key] = temp
                        }
                    }
            }
            hasChanges.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        if(::listener.isInitialized) listener.remove()
    }
}
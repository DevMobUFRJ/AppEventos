package devmob.semanasacademicas.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.Evento
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.arrayListOf
import kotlin.collections.component2
import kotlin.collections.contains
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.hashMapOf
import kotlin.collections.iterator
import kotlin.collections.set

class SelectedWeek: ViewModel(){

    var atividade = Atividade()
    var selectedWeek = Evento()
        set(value) {
            if (value.id == field.id) {
                return
            } else if(true){
                if(::listener.isInitialized){
                    typeList.clear()
                    listener.remove()
                }
                atividades.clear()
                typeList = hashMapOf()
                listener = createListener(value.id)
                field = value
            }
        }
    lateinit var listener: ListenerRegistration

    val atividades = HashMap<String, ArrayList<Atividade>>()
    val hasChanges = MutableLiveData<Boolean>()

    var filtered = HashMap<String, ArrayList<Atividade>>()
    var typeList = hashMapOf<String, Int>()

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

                typeList[Types.all] = typeList[Types.all]?.plus(1) ?: 1
                typeList[temp.tipo] = typeList[temp.tipo]?.plus(1) ?: 1

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
//            filterBy(tipo)
            hasChanges.postValue(true)
//            Log.d("aaaaaa", typeList.keys.toString())
        }

    fun filterBy(tipo: String) {
        filtered = hashMapOf<String, ArrayList<Atividade>>().apply {
            atividades.forEach {
                val temp = it.value.filter { tipo == Types.all || it.tipo == tipo }
                if (temp.isNotEmpty()) this[it.key] = temp as ArrayList<Atividade>
            }
        }
        hasChanges.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        if(::listener.isInitialized) listener.remove()
    }
}
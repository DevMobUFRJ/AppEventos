package devmob.semanasacademicas.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.*
import java.util.ArrayList

class MinhaSemana: ViewModel() {

    var hasValue = false
    var mAuth = ""
    private lateinit var listener: ListenerRegistration

    var atividades = MutableLiveData<HashMap<String, ArrayList<Atividade>>>()
        get() {
            if(!hasValue) {
                hasValue = true
                listener = loadFavorites()
            }
            return field
        }

    private var tempHM = hashMapOf<String, ArrayList<Atividade>>()

//    fun loadFavorites() = FirebaseFirestore.getInstance().users[mAuth].favorites.get().addOnSuccessListener {
//        val tempHM = hashMapOf<String, ArrayList<Atividade>>()
//        for(doc in it.documents){
//            val weekIdDoc = doc["weekId"] as String
//            val activityIdDoc = doc["id"] as String
//            FirebaseFirestore.getInstance().weeks[weekIdDoc].activities[activityIdDoc].get().addOnSuccessListener { docSnapshot ->
//                val temp = docSnapshot.toObject(Atividade::class.java)!!
//                with(temp) {
//                    id = activityIdDoc
//                    weekId = weekIdDoc
//                    inicio.formataBarra().also {
//                        if (it !in tempHM) tempHM[it] = ArrayList()
//                        tempHM[it]!!.add(this)
//                    }
//                }
//                atividades.postValue(tempHM)
//            }
//        }
//    }
    private fun loadFavorites() = FirebaseFirestore.getInstance().users[mAuth].favorites
        .addSnapshotListener { snapshot, _ ->
            if(snapshot != null) {
                for (docChange in snapshot.documentChanges) {
                    val weekId = docChange.document["weekId"] as String
                    val activityId = docChange.document["id"] as String

                    when(docChange.type){
                        DocumentChange.Type.ADDED -> FirebaseFirestore.getInstance().weeks[weekId].activities[activityId].get().addOnSuccessListener { docSnapshot ->
                            val temp = docSnapshot.toObject(Atividade::class.java)!!

                            temp.id = activityId
                            temp.weekId = weekId

                            temp.inicio.formataBarra().also {
                                if(it !in tempHM) tempHM[it] = ArrayList()
                                tempHM[it]!!.add(temp)
                            }
                            postClone()
                        }
                        DocumentChange.Type.REMOVED -> {

                            for(entry in tempHM){
                                val lista = entry.value
                                val busca = lista.find { it.id == activityId }
                                if (busca != null) {
                                    lista.remove(busca)
                                    if (lista.size < 1) tempHM.remove(entry.key)
                                    break
                                }
                            }
                            postClone()
                        }
                        DocumentChange.Type.MODIFIED -> TODO()
                    }
                }
            }
        }

    private fun postClone() {
        val temp = hashMapOf<String, ArrayList<Atividade>>()
        for(entry in tempHM) temp[entry.key] = entry.value.clone() as ArrayList<Atividade>
        atividades.postValue(temp)
    }

    override fun onCleared() {
        listener.remove()
        super.onCleared()
    }
}

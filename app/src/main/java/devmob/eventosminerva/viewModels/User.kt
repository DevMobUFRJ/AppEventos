package devmob.eventosminerva.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.eventosminerva.*
import devmob.eventosminerva.dataclass.Atividade


class User: ViewModel() {
    var favoriteActivities = hashMapOf<String, MutableList<Pair<String, String> > >()
    var favoriteEvents = hashMapOf<String, String>()
    var changed = MutableLiveData<Boolean>()

    val bd = FirebaseFirestore.getInstance()

    var user: FirebaseUser? = null
    var logged = MutableLiveData<Boolean>()
    private lateinit var listener: ListenerRegistration

    var atividades = MutableLiveData<HashMap<String, MutableList<Atividade>>>()
    private var tempHM = hashMapOf<String, MutableList<Atividade>>()

    init{
        createListener()
    }

    private fun createListener() = FirebaseAuth.getInstance().addAuthStateListener {
        clearAll()

        user = it.currentUser
        logged.value = user != null

        if(logged.value == true)
            listener = createFavoriteListener()
    }

    private fun clearAll(){
        if(::listener.isInitialized) listener.remove()

        favoriteActivities.clear()
        favoriteEvents.clear()
        tempHM.clear()

        postClone()
    }

    private fun createFavoriteListener() = bd.users[user!!.uid].favorites.addSnapshotListener { snapshot, _ ->
        Log.e("seila", favoriteActivities.toString())
        if(snapshot != null) {
            for(docChange in snapshot.documentChanges) {
                if(docChange.document["id"] == null) weekListener(docChange)
                else activityListener(docChange)
            }
            changed.postValue(true)
        }
    }

    private fun weekListener(docChange: DocumentChange) {
        val weekId = docChange.document["weekId"] as String
        when(docChange.type){
            ADDED -> favoriteEvents[weekId] = docChange.document.id
            MODIFIED -> TODO()
            REMOVED -> favoriteEvents.remove(weekId)
        }
    }

    private fun activityListener(docChange: DocumentChange) {
        val weekId = docChange.document["weekId"] as String
        val activityId = docChange.document["id"] as String

        var pair = Pair(activityId, docChange.document.id)
        if(!favoriteActivities.containsKey(weekId)) favoriteActivities[weekId] = mutableListOf()

        favoriteActivities[weekId]?.apply {
            when(docChange.type) {
                ADDED -> {
                    loadNewAct(weekId, activityId)
                    add(pair)
                }
                MODIFIED -> {}
                REMOVED -> {
                    for(p in this) if (p.first == pair.first) pair = p
                    remove(pair)
                    removeAct(weekId, activityId)
                }
            }
        }
    }

    fun addFavorite(weekId: String, activityId: String) = bd.users[user!!.uid].favorites.add(
        HashMap<String, Any>().apply {
            if(activityId.isNotBlank()) this[ACTIVITY_ID] = activityId
            this[WEEK_ID] = weekId
        })

    fun removeFavorite(favoriteId: String) = bd.users[user!!.uid].favorites[favoriteId].delete()

    private fun loadNewAct(weekId: String, activityId: String) = bd.weeks[weekId].activities[activityId].get().addOnSuccessListener { docSnapshot ->
        docSnapshot?.toObject(Atividade::class.java)?.run {
            this.id = activityId
            this.weekId = weekId
            this.inicio.formataBarra().also {
                if(it !in tempHM) tempHM[it] = mutableListOf()
                tempHM[it]!!.add(this)
            }
        }
        postClone()
    }

    private fun removeAct(weekId: String, activityId: String) {
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

    private fun postClone() {
        val temp = hashMapOf<String, MutableList<Atividade>>()
        for(entry in tempHM) temp[entry.key] = entry.value.toMutableList()
        atividades.postValue(temp)
    }

    override fun onCleared() {
        if(::listener.isInitialized) listener.remove()
        super.onCleared()
    }

}
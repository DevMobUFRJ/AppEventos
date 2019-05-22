package devmob.semanasacademicas.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.*
import devmob.semanasacademicas.dataclass.Atividade
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class User: ViewModel() {
    var favorites = hashMapOf<String, MutableList<Pair<String, String> > >()
    var changed = MutableLiveData<Boolean>()

    var user: FirebaseUser? = null
    var logged = MutableLiveData<Boolean>()
    private lateinit var listener: ListenerRegistration

    var atividades = MutableLiveData<HashMap<String, MutableList<Atividade>>>()
    private var tempHM = hashMapOf<String, MutableList<Atividade>>()

    init{
        createListener()
    }

    private fun createListener() = FirebaseAuth.getInstance().addAuthStateListener {
        user = it.currentUser
        logged.value = user != null
        if(logged.value == true) {
            if(::listener.isInitialized) listener.remove()
            favorites.clear()

            listener = createFavoriteListener()
        }
    }

    private fun createFavoriteListener() = FirebaseFirestore.getInstance().users[user!!.uid].favorites.addSnapshotListener { snapshot, _ ->
        Log.e("seila", favorites.toString())
        if(snapshot != null) {
            for(docChange in snapshot.documentChanges) {
                val weekId = docChange.document["weekId"] as String
                val activityId = docChange.document["id"] as String

                var pair = Pair(activityId, docChange.document.id)
                if(!favorites.containsKey(weekId)) favorites[weekId] = mutableListOf()

                favorites[weekId]?.apply {
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
            changed.postValue(true)
        }
    }

    fun addFavorite(weekId: String, activityId: String) = FirebaseFirestore.getInstance().users[user!!.uid].favorites.add(
        HashMap<String, Any>().apply {
            this[ACTIVITY_ID] = activityId
            this[WEEK_ID] = weekId
        })

    fun removeFavorite(favoriteId: String) = FirebaseFirestore.getInstance().users[user!!.uid].favorites[favoriteId].delete()

    private fun loadNewAct(weekId: String, activityId: String) = FirebaseFirestore.getInstance().weeks[weekId].activities[activityId].get().addOnSuccessListener { docSnapshot ->
        docSnapshot.toObject(Atividade::class.java)!!.run {
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
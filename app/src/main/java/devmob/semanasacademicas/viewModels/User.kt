package devmob.semanasacademicas.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import devmob.semanasacademicas.*


class User: ViewModel() {
    var favorites = hashMapOf<String, MutableList<Pair<String, String> > >()
    var changed = MutableLiveData<Boolean>()

    var user: FirebaseUser? = null
    var logged = MutableLiveData<Boolean>()
    private lateinit var listener: ListenerRegistration

    init{
        createListener()
    }

    private fun createFavoriteList() = FirebaseFirestore.getInstance().users[user!!.uid].favorites.addSnapshotListener { snapshot, _ ->
        Log.e("seila", favorites.toString())
        if(snapshot != null) {
            for(docChange in snapshot.documentChanges) {
                val weekId = docChange.document["weekId"] as String
                val activityId = docChange.document["id"] as String

                var pair = Pair(activityId, docChange.document.id)
                if(!favorites.containsKey(weekId)) favorites[weekId] = mutableListOf()

                favorites[weekId]?.apply {
                    when(docChange.type) {
                        ADDED -> add(pair)
                        MODIFIED -> {}
                        REMOVED -> {
                            for(p in this) if (p.first == pair.first) pair = p
                            remove(pair)
                        }
                    }
                }

            }
            changed.postValue(true)
        }
    }

    fun addFavorite(weekId: String, activityId: String): Task<DocumentReference> {
        val data = HashMap<String, Any>()

        data[ACTIVITY_ID] = activityId
        data[WEEK_ID] = weekId

        return FirebaseFirestore.getInstance().users[user!!.uid].favorites.add(data)
    }

    fun removeFavorite(favoriteId: String) = FirebaseFirestore.getInstance().users[user!!.uid].favorites[favoriteId].delete()

    private fun createListener() = FirebaseAuth.getInstance().addAuthStateListener {
        user = it.currentUser
        logged.value = user != null
        if(logged.value == true) {
            if(::listener.isInitialized) listener.remove()
            favorites.clear()

            listener = createFavoriteList()
        }
    }

    override fun onCleared() = listener.remove()

}
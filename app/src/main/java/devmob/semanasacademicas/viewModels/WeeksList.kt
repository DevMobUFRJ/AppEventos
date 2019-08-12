package devmob.semanasacademicas.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.dataclass.Atividade
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.weeks

class WeeksList: ViewModel() {

    private var hasValue = false
    var screen: Int? = null
    var query = ""
    var copy = mutableListOf<Evento>()

    var hasChanges = false
    var weeks = MutableLiveData<MutableList<Evento>>()
        get() {
            if(!hasValue) loadWeeks()
            return field
        }

    fun makeQuery()
            = setWeeks(copy.filter { (it).nome.contains(query, ignoreCase = true) } as MutableList)

    fun setWeeks(weeksList: MutableList<Evento>){
        hasChanges = true
        hasValue = true
        weeks.postValue(weeksList)
    }

    fun loadWeeks() = FirebaseFirestore.getInstance().weeks.get().addOnSuccessListener {
        val temp = mutableListOf<Evento>()
        for (document in it.documents) {

            var aux =
            if(document.data?.get("apresentador") == null && document.data?.get("grupo") == null) document.toObject(Evento::class.java)
            else document.toObject(Atividade::class.java)

            Log.d("aaaaaaa", aux.toString())
            aux!!.id = document.id
            temp.add(aux)
        }
        copy = temp
        makeQuery()
    }
}
package devmob.semanasacademicas.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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

    var weeks = MutableLiveData<MutableList<Evento>>()
        get() {
            if(!hasValue) loadWeeks()
            return field
        }

    fun makeQuery()
            = setWeeks(copy.filter { it.nome.contains(query, ignoreCase = true) } as MutableList<Evento>)

    fun setWeeks(weeksList: MutableList<Evento>){
        weeks.postValue(weeksList)
        hasValue = true
    }

    fun loadWeeks() = FirebaseFirestore.getInstance().weeks.get().addOnSuccessListener {
        val temp = mutableListOf<Evento>()
        for (document in it.documents) {

            var aux =
            if(document.data?.get("apresentador") == null && document.data?.get("grupo") == null) document.toObject(Evento::class.java)
            else document.toObject(Atividade::class.java)

            aux!!.id = document.id
            temp.add(aux)
        }
        setWeeks(temp)
        copy = temp
    }
}
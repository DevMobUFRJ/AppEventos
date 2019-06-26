package devmob.semanasacademicas.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.weeks

class WeeksList: ViewModel() {

    private var hasValue = false
    var screen: Int? = null
    var item = Evento()
    var copy = mutableListOf<Evento>()

    var weeks = MutableLiveData<MutableList<Evento>>()
        get() {
            if(!hasValue) loadWeeks()
            return field
        }

    fun setWeeks(weeksList: MutableList<Evento>){
        weeks.postValue(weeksList)
        hasValue = true
    }

    fun loadWeeks() = FirebaseFirestore.getInstance().weeks.get().addOnSuccessListener {
        val temp = mutableListOf<Evento>()
        for (document in it.documents) {
            val aux = document.toObject(Evento::class.java)
            aux!!.id = document.id
            temp.add(aux)
        }
        setWeeks(temp)
        copy = temp
    }
}
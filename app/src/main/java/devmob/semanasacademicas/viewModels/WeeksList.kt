package devmob.semanasacademicas.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.dataclass.Evento
import devmob.semanasacademicas.weeks

class WeeksList: ViewModel() {

    private var hasValue = false
    var screen: Int? = null
    var item = Evento()

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
    }
}
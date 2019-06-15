package devmob.semanasacademicas.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import devmob.semanasacademicas.activities
import devmob.semanasacademicas.get
import devmob.semanasacademicas.weeks

class Atividades: ViewModel(){
    var week = MutableLiveData<String>()
}
package devmob.semanasacademicas.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import devmob.semanasacademicas.R
import devmob.semanasacademicas.Types
import devmob.semanasacademicas.adapters.TypeListAdapter
import devmob.semanasacademicas.databinding.ContentEventDetailsBinding
import devmob.semanasacademicas.viewModels.SelectedWeek

class EventDetails : Fragment() {
    lateinit var model: SelectedWeek

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View?
            = ContentEventDetailsBinding.inflate(layoutInflater, container, false).run {
        model = ViewModelProviders.of(activity!!).get(SelectedWeek::class.java)
        event = model.selectedWeek
        fragment = this@EventDetails

        Glide.with(image).load(event?.link)
            .placeholder(R.drawable.thumb_placeholder)
            .error(R.drawable.thumb_error)
            .thumbnail(0.1f)
            .into(image)

        model.hasChanges.observe(this@EventDetails, Observer {
            val viewAdapter = TypeListAdapter(listOf(Types.all) + event!!.listaTipos, model.selectedWeek, context)
            val viewManager = LinearLayoutManager(context)

            listaDeTipos.apply {
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                layoutManager = viewManager
                adapter = viewAdapter
            }
        })
        return root
    }
}
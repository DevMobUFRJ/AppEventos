package devmob.semanasacademicas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class AtividadesListAdapter(val context: Context, val headers: List<String>, val items: HashMap<String, ArrayList<Atividade>>): BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int) = headers[groupPosition]

    override fun getGroupCount() = headers.size

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val titulo = getGroup(groupPosition)

        val convertView2: View?
        if(convertView == null){
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView2 = layoutInflater.inflate(R.layout.atividades_lista_headers, null)
        }else{
            convertView2 = convertView
        }

        val tituloView = convertView2!!.findViewById<TextView>(R.id.atividadesHeader)
        tituloView.text = titulo

        return convertView2
    }

    override fun getChild(groupPosition: Int, childPosition: Int) = items[headers[groupPosition]]!![childPosition]

    override fun getChildrenCount(groupPosition: Int) = items[headers[groupPosition]]!!.size

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val atividade = getChild(groupPosition, childPosition)
        val convertView2: View?

        if(convertView == null){
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView2 = layoutInflater.inflate(R.layout.atividades_lista_items, null)
        }else{
            convertView2 = convertView
        }

        val nomeView = convertView2!!.findViewById<TextView>(R.id.itemListaNome)
        nomeView.text = atividade.nome

        val tipoView = convertView2.findViewById<TextView>(R.id.itemListaTipo)
        tipoView.text = atividade.tipo[0].toTitleCase() + atividade.tipo.substring(1)

        return convertView2

    }



    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()


}
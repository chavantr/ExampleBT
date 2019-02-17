package imei.mywings.com.bustrackingapp.joint

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import imei.mywings.com.bustrackingapp.Bus
import imei.mywings.com.bustrackingapp.OnSelectedBusListener
import kotlinx.android.synthetic.main.layout_row.view.*


class JointAdapter(var list: List<Bus>) : RecyclerView.Adapter<JointAdapter.JointViewHolder>() {

    private var lst = list;
    private lateinit var onSelectedBusListener: OnSelectedBusListener

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): JointViewHolder {
        return JointViewHolder(
            LayoutInflater.from(parent.context).inflate(
                imei.mywings.com.bustrackingapp.R.layout.layout_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = lst.size

    override fun onBindViewHolder(viewHolder: JointViewHolder, position: Int) {
        viewHolder.lblName.text = lst[position].name
        viewHolder.lblName.setOnClickListener {
            onSelectedBusListener.onBusSelected(lst[position])
        }
    }

    inner class JointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblName = itemView.lblName!!
    }

    fun setOnSelectedBusListener(onSelectedBusListener: OnSelectedBusListener) {
        this.onSelectedBusListener = onSelectedBusListener
    }
}
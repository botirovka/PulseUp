package com.example.pulseup.ui.setUp.weightHeight

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseup.R

class WeightHeightAdapter(
    private val itemList: List<Int>,
    private val viewType: Int,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<WeightHeightAdapter.ItemViewHolder>() {

    companion object {
        const val TYPE_WEIGHT = 0
        const val TYPE_HEIGHT = 1
    }

    private var selectedItemPos: Int = -1

    fun setSelectedItem(position: Int) {
        val oldSelectedPos = selectedItemPos
        selectedItemPos = position
        notifyItemChanged(oldSelectedPos)
        notifyItemChanged(position)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItem: TextView = itemView.findViewById(R.id.tvItem)
        val viewCenterStick: View = itemView.findViewById(R.id.view_center_stick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = if (viewType == TYPE_WEIGHT) R.layout.item_weight_rv else R.layout.item_height_rv
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val value = itemList[position]
        holder.tvItem.text = "$value"
        if (position == selectedItemPos) {
            holder.tvItem.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
            holder.viewCenterStick.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.lime_green))
        } else {
            holder.tvItem.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white_A80))
            holder.tvItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f)
            holder.viewCenterStick.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}
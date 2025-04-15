package com.example.pulseup.ui.setUp.age

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseup.R

class AgeAdapter(private val ageList: List<Int>, private val onClick: (Int) -> Unit) :
    RecyclerView.Adapter<AgeAdapter.AgeViewHolder>() {
        private var selectedItemPos: Int = -1

    fun setSelectedItem(position: Int){
        val oldSelectedPos = selectedItemPos
        selectedItemPos = position
        notifyItemChanged(oldSelectedPos)
        notifyItemChanged(position)
    }

    inner class AgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_age_rv, parent, false)
        return AgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgeViewHolder, position: Int) {
        val age = ageList[position]
        holder.tvAge.text = age.toString()
        if(position == selectedItemPos){
            holder.tvAge.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.tvAge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f)
        }
        else{
            holder.tvAge.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black_A60))
            holder.tvAge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f)
        }
        holder.itemView.setOnClickListener { onClick(position) }
    }

    override fun getItemCount(): Int = ageList.size
}
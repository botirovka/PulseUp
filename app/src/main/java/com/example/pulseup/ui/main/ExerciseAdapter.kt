package com.example.pulseup.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.models.Exercise
import com.example.pulseup.R
import com.example.pulseup.formatTime

class ExerciseAdapter() : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {
    private var exercisesList: List<Exercise> = emptyList()

    fun submitList(newItems: List<Exercise>) {
        exercisesList = newItems
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivExerciseGifImage)
        val tvExerciseTitle: TextView = itemView.findViewById(R.id.tvExerciseTitle)
        val tvExerciseTime: TextView = itemView.findViewById(R.id.tvExerciseTime)
        val tvExerciseRepetitions: TextView = itemView.findViewById(R.id.tvExerciseRepetitions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercisesList[position]
        if (exercise.imageUrl.isNotBlank()){
            Glide.with(holder.itemView.context).load(exercise.imageUrl)
                .into(holder.ivImage)
        }
        holder.tvExerciseTitle.text = exercise.title
        holder.tvExerciseTime.text = exercise.duration.formatTime()
        holder.tvExerciseRepetitions.text = "${exercise.repetitions}x"
    }

    override fun getItemCount(): Int = exercisesList.size
}
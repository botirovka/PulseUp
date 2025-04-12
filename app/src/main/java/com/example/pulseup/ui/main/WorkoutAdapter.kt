package com.example.pulseup.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.models.Workout
import com.example.pulseup.R

class WorkoutAdapter(
    private val onClick: (Workout) -> Unit,
    private val viewType: Int
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {
    private var workoutList: List<Workout> = emptyList()

    companion object {
        const val TYPE_FULL_WIDTH = 0
        const val TYPE_SQUARE = 1
    }

    fun submitList(newItems: List<Workout>) {
        workoutList = newItems
        notifyDataSetChanged()
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvWorkoutTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvWorkoutTime)
        val tvCalories: TextView = itemView.findViewById(R.id.tvWorkoutCaloriesCount)
        val tvExercisesCount: TextView? = itemView.findViewById(R.id.tvWorkoutExercisesCount)
        val ivImage: ImageView = itemView.findViewById(R.id.ivWorkoutImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val layout = if (viewType == TYPE_FULL_WIDTH) R.layout.item_workout_full_width else R.layout.item_workout_square
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.tvTitle.text = workout.title
        holder.tvTime.text = "${workout.duration} Minutes"
        holder.tvCalories.text = "${workout.calories} Kcal"
        holder.tvExercisesCount?.text = "${workout.exercisesId.size} Exercises"
        if (workout.imageUrl.isNotBlank()){
            Glide.with(holder.itemView.context).load(workout.imageUrl)
                .into(holder.ivImage)
        }
        holder.itemView.setOnClickListener { onClick(workout) }
    }

    override fun getItemCount(): Int = workoutList.size

    override fun getItemViewType(position: Int): Int {
        return viewType
    }
}
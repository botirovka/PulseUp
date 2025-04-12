package com.example.domain.models

data class Workout(
    var id: String = "",
    val title: String = "",
    val duration: Int = 0,
    val calories: Int = 0,
    val exercisesCount: Int = 0,
    val exercisesId: List<String> = emptyList(),
    val imageUrl: String = ""
)

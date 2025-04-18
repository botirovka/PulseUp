package com.example.domain.models

data class User(
    val fullName: String = "",
    val nickName: String = "",
    val email: String = "",
    val gender: Genders = Genders.NONE,
    val age: Int = 0,
    val weight: Int = 0,
    val height: Int = 0,
    val goal: Goals = Goals.NONE,
    val level: Levels = Levels.NONE,
    val historyWorkouts: List<String> = emptyList(),
    val totalWorkoutDuration: Int = 0,
    val totalWorkoutCount: Int = 0,
    val totalWorkoutCalories: Int = 0

)

    fun User.isFilled() =
        fullName.isNotBlank() and
        nickName.isNotBlank() and
        email.isNotBlank() and
        (gender == Genders.NONE).not() and
        (age > 0) and
        (weight > 0) and
        (height > 0) and
        (goal == Goals.NONE).not() and
        (level == Levels.NONE).not()



package com.example.domain.models

data class UserDto(
    var fullName: String = "",
    var nickName: String = "",
    var email: String = "",
    var gender: String = "",
    var age: String = "",
    var weight: String = "",
    var height: String = "",
    var goal: String = "",
    var level: String = "",
    var historyWorkouts: List<String> = emptyList(),
    var totalWorkoutDuration: Int = 0,
    var totalWorkoutCount: Int = 0,
    var totalWorkoutCalories: Int = 0
)

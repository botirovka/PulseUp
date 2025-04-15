package com.example.domain.mappers

import com.example.domain.models.Genders
import com.example.domain.models.Goals
import com.example.domain.models.Levels
import com.example.domain.models.User
import com.example.domain.models.UserDto

fun User.toUserDto(): UserDto{
    return UserDto(
        this.fullName,
        this.nickName,
        this.email,
        this.gender.toString(),
        this.age.toString(),
        this.weight.toString(),
        this.height.toString(),
        this.goal.toString(),
        this.level.toString(),
        this.historyWorkouts,
        this.totalWorkoutDuration,
        this.totalWorkoutCount,
        this.totalWorkoutCalories
    )
}

fun UserDto.toUser(): User{
    return User(
        this.fullName,
        this.nickName,
        this.email,
        Genders.valueOf(this.gender),
        this.age.toIntOrNull() ?: 0,
        this.weight.toIntOrNull() ?: 0,
        this.height.toIntOrNull() ?: 0,
        Goals.valueOf(this.goal),
        Levels.valueOf(this.level),
        this.historyWorkouts,
        this.totalWorkoutDuration,
        this.totalWorkoutCount,
        this.totalWorkoutCalories
    )
}
package com.example.pulseup

import android.util.Patterns

fun String.validateEmail(): Boolean {
    val email = this
    val emailPattern = Patterns.EMAIL_ADDRESS
    return emailPattern.matcher(email).matches()
}

fun String.validatePassword(): Boolean {
    return this.length >= 8
}
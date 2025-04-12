package com.example.data.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.models.User
import com.google.gson.Gson

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USER_DATA = "user_data"
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_USER_DATA, userJson)
            .apply()
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER_DATA, null)
        return userJson?.let {
            gson.fromJson(it, User::class.java)
        }
    }

    fun clearUser() {
        sharedPreferences.edit()
            .remove(KEY_USER_DATA)
            .apply()
    }
}
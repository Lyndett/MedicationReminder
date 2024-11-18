package com.example.medicamentoreminder

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EventUtils {

    fun loadEvents(sharedPreferences: SharedPreferences): List<Event> {
        val json = sharedPreferences.getString("events", null)
        val type = object : TypeToken<List<Event>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun saveEvents(sharedPreferences: SharedPreferences, events: List<Event>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(events)
        editor.putString("events", json)
        editor.apply()
    }

    fun getEventsFromSharedPreferences(context: Context): List<Event> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("events", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("events", null)
        val type = object : TypeToken<List<Event>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()

    }
}
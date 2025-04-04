package com.example.farmmobileapp.core.data.preferences.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.mapboxDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mapbox_preferences"
)

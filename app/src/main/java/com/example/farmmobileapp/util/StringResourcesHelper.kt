package com.example.farmmobileapp.util

import android.content.Context
import androidx.annotation.StringRes

class StringResourcesHelper(private val context: Context) {
    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}
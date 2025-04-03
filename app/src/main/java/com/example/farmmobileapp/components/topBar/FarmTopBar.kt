package com.example.farmmobileapp.components.topBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmTopBar(title: @Composable() () -> Unit, actions: List<TopBarAction>) {
    TopAppBar(
        title = title,
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(imageVector = action.icon, contentDescription = action.icon.name)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}
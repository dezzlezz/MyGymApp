package com.example.mygymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(onBack: () -> Unit, viewModel: ThemeViewModel = viewModel()) {
    val current by viewModel.currentTheme.collectAsState()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Select Theme") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            }
        )
    }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            AppTheme.values().forEach { theme ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.setTheme(theme) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (theme == current) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        RadioButton(selected = theme == current, onClick = { viewModel.setTheme(theme) })
                        Text(theme.displayName, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

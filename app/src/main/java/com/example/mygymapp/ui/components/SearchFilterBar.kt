package com.example.mygymapp.ui.components

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun SearchFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    favoritesOnly: Boolean,
    onFavoritesToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search plans") },
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onFavoritesToggle) {
            Icon(
                imageVector = if (favoritesOnly) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = if (favoritesOnly) "Show all" else "Show favorites"
            )
        }
    }
}
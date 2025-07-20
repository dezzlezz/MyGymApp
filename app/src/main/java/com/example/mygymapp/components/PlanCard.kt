package com.example.mygymapp.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.data.Plan
import com.example.mygymapp.ui.widgets.DifficultyRating
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

@Composable
fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlanIcon(iconUriString = plan.iconUri)
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium
                )
                DifficultyRating(rating = plan.difficulty)
            }
            if (plan.isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(id = R.string.favorite_marked),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PlanIcon(iconUriString: String?) {
    val uri = iconUriString?.let { Uri.parse(it) }
    if (uri != null) {
        val painter = rememberAsyncImagePainter(uri)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            Icons.Filled.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}

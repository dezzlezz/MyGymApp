package com.example.mygymapp.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.LocalIndication
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
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.data.Plan
import com.example.mygymapp.ui.widgets.DifficultyRating
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import androidx.compose.foundation.LocalIndication

@Composable
fun PlanCard(
    plan: Plan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .indication(interactionSource, LocalIndication.current)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
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
                    imageVector = Icons.Outlined.Star,
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
            Icons.Outlined.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
    }
}

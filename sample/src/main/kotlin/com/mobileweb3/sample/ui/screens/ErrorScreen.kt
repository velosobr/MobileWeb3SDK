package com.mobileweb3.sample.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mobileweb3.sample.ui.components.PrimaryButton
import com.mobileweb3.sample.ui.components.StatusIcon

@Composable
fun ErrorScreen(
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StatusIcon(
            emoji = "⚠️",
            backgroundColor = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Ops! Algo deu errado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        PrimaryButton(
            text = "Tentar novamente",
            onClick = onRetryClick,
            modifier = Modifier.width(200.dp)
        )
    }
}

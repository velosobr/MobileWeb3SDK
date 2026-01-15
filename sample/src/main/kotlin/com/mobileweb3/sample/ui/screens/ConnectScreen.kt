package com.mobileweb3.sample.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mobileweb3.sample.ui.components.InfoCard
import com.mobileweb3.sample.ui.components.PrimaryButton
import com.mobileweb3.sample.ui.components.StatusIcon

@Composable
fun ConnectScreen(
    onConnectClick: () -> Unit,
    isConnecting: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatusIcon(
                emoji = "🔐",
                backgroundColor = MaterialTheme.colorScheme.primary
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Token Gate Demo",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Conecte sua wallet para verificar se você tem acesso ao conteúdo exclusivo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        
        // Info Card
        InfoCard(title = "Como funciona?") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StepItem(number = "1", text = "Conecte sua wallet (MetaMask)")
                StepItem(number = "2", text = "Verificamos seu balance de tokens")
                StepItem(number = "3", text = "Se você tiver o token, acesso liberado!")
            }
        }
        
        // Button
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = if (isConnecting) "Conectando..." else "Conectar Wallet",
                onClick = onConnectClick,
                loading = isConnecting
            )
            
            Text(
                text = "Polygon Amoy Testnet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun StepItem(number: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

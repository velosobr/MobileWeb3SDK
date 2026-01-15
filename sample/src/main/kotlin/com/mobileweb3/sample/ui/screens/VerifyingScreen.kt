package com.mobileweb3.sample.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobileweb3.sample.ui.components.WalletAddressChip

@Composable
fun VerifyingScreen(
    walletAddress: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WalletAddressChip(address = walletAddress)
        
        Spacer(modifier = Modifier.height(60.dp))
        
        CircularProgressIndicator(
            modifier = Modifier.size(72.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Verificando acesso...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Consultando seu balance na blockchain",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

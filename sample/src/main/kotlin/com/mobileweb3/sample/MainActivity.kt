package com.mobileweb3.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobileweb3.sample.ui.theme.Web3DemoTheme
import com.mobileweb3.sample.ui.screens.*

/**
 * Activity principal do app
 * 
 * Usa Jetpack Compose para renderizar a UI.
 * A tela mostrada depende do estado do MainViewModel.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Permite que o app desenhe atrás das barras do sistema
        enableEdgeToEdge()
        
        setContent {
            Web3DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

/**
 * Tela principal que gerencia navegação entre estados
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Renderiza a tela apropriada baseada no estado
    when (val state = uiState) {
        is UiState.Connect -> {
            ConnectScreen(
                onConnectClick = { viewModel.connect() },
                isConnecting = false
            )
        }
        
        is UiState.Connecting -> {
            ConnectScreen(
                onConnectClick = { },
                isConnecting = true
            )
        }
        
        is UiState.Verifying -> {
            VerifyingScreen(
                walletAddress = state.address
            )
        }
        
        is UiState.Granted -> {
            AccessGrantedScreen(
                walletAddress = state.address,
                tokenBalance = state.balance,
                onDisconnect = { viewModel.disconnect() }
            )
        }
        
        is UiState.Denied -> {
            AccessDeniedScreen(
                walletAddress = state.address,
                currentBalance = state.currentBalance,
                requiredBalance = state.requiredBalance,
                onBuyClick = { viewModel.onBuyClick() },
                onDisconnect = { viewModel.disconnect() }
            )
        }
        
        is UiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetryClick = { viewModel.retry() }
            )
        }
    }
}

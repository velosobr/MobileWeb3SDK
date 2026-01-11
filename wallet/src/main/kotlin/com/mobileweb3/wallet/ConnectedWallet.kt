package com.mobileweb3.wallet

/**
 * Representa uma wallet conectada
 */
data class ConnectedWallet(
    val address: String,
    val chainId: Int
)

/**
 * Estados possíveis da wallet
 */
sealed class WalletState {
    
    /** Nenhuma wallet conectada */
    data object Disconnected : WalletState()
    
    /** Conectando... */
    data object Connecting : WalletState()
    
    /** Wallet conectada com sucesso */
    data class Connected(val wallet: ConnectedWallet) : WalletState()
    
    /** Erro na conexão */
    data class Error(val message: String, val cause: Throwable? = null) : WalletState()
}

/**
 * Resultado da tentativa de conexão
 */
sealed class ConnectResult {
    data class Success(val wallet: ConnectedWallet) : ConnectResult()
    data object Cancelled : ConnectResult()
    data class Error(val message: String, val cause: Throwable? = null) : ConnectResult()
}
package com.mobileweb3.wallet

import android.content.Context
import com.mobileweb3.core.AppMetadata
import com.mobileweb3.core.Chain
import kotlinx.coroutines.flow.StateFlow

/**
 * Ponto de entrada do módulo Wallet
 */
class WalletModule(
    context: Context,
    projectId: String,
    appMetadata: AppMetadata,
    chain: Chain
) {
    private val walletManager = WalletManager(context, projectId, appMetadata, chain)

    /** Estado atual da wallet */
    val state: StateFlow<WalletState>
        get() = walletManager.walletState

    /** Verifica se está conectado */
    val isConnected: Boolean
        get() = walletManager.isConnected

    /** Wallet conectada atual (ou null) */
    val currentWallet: ConnectedWallet?
        get() = walletManager.currentWallet

    /** Endereço da wallet atual (ou null) */
    val address: String?
        get() = currentWallet?.address

    /**
     * Inicializa o módulo (chamar no Application.onCreate)
     */
    fun initialize() {
        walletManager.initialize()
    }

    /**
     * Conecta com uma wallet
     */
    suspend fun connect(timeoutSeconds: Long = 60): ConnectResult {
        return walletManager.connect(timeoutSeconds)
    }

    /**
     * Conecta usando URI (QR code)
     */
    fun connectWithUri(uri: String, onResult: (ConnectResult) -> Unit) {
        walletManager.connectWithUri(uri, onResult)
    }

    /**
     * Abre MetaMask
     */
    fun openMetaMask(uri: String) {
        walletManager.openMetaMask(uri)
    }

    /**
     * Desconecta
     */
    fun disconnect() {
        walletManager.disconnect()
    }

    /**
     * Define wallet manualmente (para testes/demo)
     */
    fun setConnectedWallet(address: String, chainId: Int? = null) {
        walletManager.setConnectedWallet(address, chainId ?: 80001)
    }
}
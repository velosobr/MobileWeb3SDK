package com.mobileweb3.wallet

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mobileweb3.core.AppMetadata
import com.mobileweb3.core.Chain
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

/**
 * Gerencia conexão com wallets via WalletConnect v2
 */
class WalletManager(
    private val context: Context,
    private val projectId: String,
    private val appMetadata: AppMetadata,
    private val chain: Chain
) {
    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private var currentSession: String? = null

    val isConnected: Boolean
        get() = _walletState.value is WalletState.Connected

    val currentWallet: ConnectedWallet?
        get() = (_walletState.value as? WalletState.Connected)?.wallet

    /**
     * Inicializa o WalletConnect
     */
    fun initialize() {
        val serverUrl = "wss://relay.walletconnect.com?projectId=$projectId"
        
        val coreMetadata = Core.Model.AppMetaData(
            name = appMetadata.name,
            description = appMetadata.description,
            url = appMetadata.url,
            icons = listOf(appMetadata.iconUrl),
            redirect = "${appMetadata.name.lowercase().replace(" ", "")}://wc"
        )

        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = ConnectionType.AUTOMATIC,
            application = context.applicationContext as android.app.Application,
            metaData = coreMetadata
        ) { error ->
            _walletState.value = WalletState.Error("Core init failed: ${error.throwable.message}", error.throwable)
        }

        val walletParams = Wallet.Params.Init(core = CoreClient)
        
        Web3Wallet.initialize(walletParams) { error ->
            _walletState.value = WalletState.Error("Wallet init failed: ${error.throwable.message}", error.throwable)
        }

        setupListeners()
    }

    private fun setupListeners() {
        Web3Wallet.setWalletDelegate(object : Web3Wallet.WalletDelegate {
            override fun onSessionProposal(
                sessionProposal: Wallet.Model.SessionProposal,
                verifyContext: Wallet.Model.VerifyContext
            ) {
                // Não usado no modo dApp
            }

            override fun onSessionRequest(
                sessionRequest: Wallet.Model.SessionRequest,
                verifyContext: Wallet.Model.VerifyContext
            ) {
                // Não usado no modo dApp
            }

            override fun onAuthRequest(
                authRequest: Wallet.Model.AuthRequest,
                verifyContext: Wallet.Model.VerifyContext
            ) {
                // Não usado no modo dApp
            }

            override fun onSessionDelete(sessionDelete: Wallet.Model.SessionDelete) {
                _walletState.value = WalletState.Disconnected
                currentSession = null
            }

            override fun onSessionExtend(session: Wallet.Model.Session) {
                TODO("Not yet implemented")
            }

            override fun onSessionSettleResponse(settleSessionResponse: Wallet.Model.SettledSessionResponse) {
                // Handled elsewhere
            }

            override fun onSessionUpdateResponse(sessionUpdateResponse: Wallet.Model.SessionUpdateResponse) {
                // Não usado
            }

            override fun onConnectionStateChange(state: Wallet.Model.ConnectionState) {
                // Log connection state if needed
            }

            override fun onError(error: Wallet.Model.Error) {
                _walletState.value = WalletState.Error(error.throwable.message ?: "Unknown error", error.throwable)
            }
        })
    }

    /**
     * Conecta com uma wallet
     * Abre o app da wallet para aprovação
     */
    suspend fun connect(timeoutSeconds: Long = 60): ConnectResult {
        _walletState.value = WalletState.Connecting

        return try {
            withTimeout(timeoutSeconds.seconds) {
                suspendCancellableCoroutine { continuation ->
                    // Simplified connection for MVP
                    // In production, use proper WalletConnect pairing flow
                    
                    val pairingParams = Core.Params.Pair("")
                    
                    // For MVP, we'll simulate connection
                    // Real implementation needs deep link handling
                    
                    _walletState.value = WalletState.Error("WalletConnect flow requires deep link setup", null)
                    continuation.resume(ConnectResult.Error("Deep link setup required"))
                }
            }
        } catch (e: Exception) {
            _walletState.value = WalletState.Error(e.message ?: "Connection failed", e)
            ConnectResult.Error(e.message ?: "Connection failed", e)
        }
    }

    /**
     * Conecta usando URI de pairing (para QR code)
     */
    fun connectWithUri(uri: String, onResult: (ConnectResult) -> Unit) {
        _walletState.value = WalletState.Connecting
        
        try {
            val pairingParams = Core.Params.Pair(uri)
            CoreClient.Pairing.pair(pairingParams) { error ->
                _walletState.value = WalletState.Error("Pairing failed: ${error.throwable.message}", error.throwable)
                onResult(ConnectResult.Error(error.throwable.message ?: "Pairing failed", error.throwable))
            }
        } catch (e: Exception) {
            _walletState.value = WalletState.Error(e.message ?: "Connection failed", e)
            onResult(ConnectResult.Error(e.message ?: "Connection failed", e))
        }
    }

    /**
     * Abre MetaMask para conexão
     */
    fun openMetaMask(uri: String) {
        val metamaskUri = "metamask://wc?uri=${Uri.encode(uri)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(metamaskUri)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // MetaMask não instalado, tenta abrir na Play Store
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=io.metamask")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(playStoreIntent)
        }
    }

    /**
     * Desconecta a wallet atual
     */
    fun disconnect() {
        currentSession?.let { topic ->
            val disconnectParams = Wallet.Params.SessionDisconnect(topic)
            Web3Wallet.disconnectSession(disconnectParams) { error ->
                // Log error if needed
            }
        }
        
        currentSession = null
        _walletState.value = WalletState.Disconnected
    }

    /**
     * Define manualmente uma wallet conectada (para testes/MVP)
     */
    fun setConnectedWallet(address: String, chainId: Int = chain.chainId) {
        val wallet = ConnectedWallet(address, chainId)
        _walletState.value = WalletState.Connected(wallet)
    }
}
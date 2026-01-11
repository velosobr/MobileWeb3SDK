package com.mobileweb3.core

import android.content.Context
import com.mobileweb3.contracts.ContractsModule
import com.mobileweb3.wallet.WalletModule
import com.mobileweb3.wallet.WalletState
import com.mobileweb3.wallet.ConnectResult
import com.mobileweb3.wallet.ConnectedWallet
import com.mobileweb3.contracts.AccessResult
import com.mobileweb3.contracts.TokenType
import kotlinx.coroutines.flow.StateFlow
import java.math.BigInteger

/**
 * Facade principal do Mobile Web3 SDK
 * 
 * Uso:
 * ```
 * val sdk = MobileWeb3SDK.init(context) {
 *     chain = Chain.PolygonMumbai
 *     projectId = "seu-project-id"
 *     appMetadata {
 *         name = "Meu App"
 *         url = "https://meuapp.com"
 *     }
 * }
 * 
 * // Conectar wallet
 * sdk.connect()
 * 
 * // Verificar token gating
 * val hasAccess = sdk.checkAccess(tokenContract = "0x...")
 * ```
 */
class MobileWeb3SDK private constructor(
    private val context: Context,
    val config: Web3Config
) {
    // Módulos internos
    private val rpcProvider = RpcProvider(
        rpcUrl = config.effectiveRpcUrl,
        timeout = config.requestTimeout.inWholeSeconds,
        enableLogging = config.enableLogging
    )

    private val walletModule = WalletModule(
        context = context,
        projectId = config.projectId,
        appMetadata = config.appMetadata,
        chain = config.chain
    )

    private val contractsModule = ContractsModule(rpcProvider)

    // ==================== WALLET ====================

    /** Estado atual da wallet (observe via Flow) */
    val walletState: StateFlow<WalletState>
        get() = walletModule.state

    /** Verifica se há wallet conectada */
    val isConnected: Boolean
        get() = walletModule.isConnected

    /** Wallet conectada atual */
    val currentWallet: ConnectedWallet?
        get() = walletModule.currentWallet

    /** Endereço da wallet conectada */
    val walletAddress: String?
        get() = walletModule.address

    /** Inicializa WalletConnect (chamar no Application.onCreate) */
    fun initializeWallet() {
        walletModule.initialize()
    }

    /** Conecta com wallet */
    suspend fun connect(timeoutSeconds: Long = 60): ConnectResult {
        return walletModule.connect(timeoutSeconds)
    }

    /** Desconecta wallet */
    fun disconnect() {
        walletModule.disconnect()
    }

    /** Define wallet manualmente (para testes/demo) */
    fun setWalletForTesting(address: String) {
        walletModule.setConnectedWallet(address, config.chain.chainId)
    }

    // ==================== TOKEN GATING ====================

    /** 
     * Verifica acesso simples (true/false) 
     * Usa a wallet conectada automaticamente
     */
    suspend fun checkAccess(
        tokenContract: String,
        minBalance: BigInteger = BigInteger.ONE,
        tokenType: TokenType = TokenType.ERC20
    ): Boolean {
        val address = walletAddress ?: return false
        return contractsModule.tokenGating.checkAccess(
            wallet = address,
            tokenContract = tokenContract,
            minBalance = minBalance,
            tokenType = tokenType
        )
    }

    /** 
     * Verifica acesso com detalhes completos
     * Retorna balance atual, required, etc.
     */
    suspend fun verifyAccess(
        tokenContract: String,
        minBalance: BigInteger = BigInteger.ONE,
        tokenType: TokenType = TokenType.ERC20
    ): AccessResult {
        val address = walletAddress 
            ?: return AccessResult.Error(NotConnectedException())
        
        return contractsModule.tokenGating.verifyAccess(
            wallet = address,
            tokenContract = tokenContract,
            minBalance = minBalance,
            tokenType = tokenType
        )
    }

    /** Verifica acesso para um endereço específico */
    suspend fun checkAccessFor(
        walletAddress: String,
        tokenContract: String,
        minBalance: BigInteger = BigInteger.ONE,
        tokenType: TokenType = TokenType.ERC20
    ): Boolean {
        return contractsModule.tokenGating.checkAccess(
            wallet = walletAddress,
            tokenContract = tokenContract,
            minBalance = minBalance,
            tokenType = tokenType
        )
    }

    // ==================== CONTRACTS ====================

    /** Acesso ao módulo de contratos para operações avançadas */
    val contracts: ContractsModule
        get() = contractsModule

    /** Acesso ao módulo de wallet para operações avançadas */
    val wallet: WalletModule
        get() = walletModule

    // ==================== UTILS ====================

    /** Chain atual */
    val chain: Chain
        get() = config.chain

    /** Verifica conexão com a blockchain */
    suspend fun checkBlockchainConnection(): Result<Int> = runCatching {
        rpcProvider.getChainId()
    }

    companion object {
        @Volatile
        private var instance: MobileWeb3SDK? = null

        /**
         * Inicializa o SDK
         */
        fun init(context: Context, block: Web3Config.Builder.() -> Unit): MobileWeb3SDK {
            val config = Web3Config.Builder().apply(block).build()
            return MobileWeb3SDK(context.applicationContext, config).also {
                instance = it
            }
        }

        /**
         * Retorna instância atual
         */
        fun getInstance(): MobileWeb3SDK {
            return instance ?: throw IllegalStateException(
                "MobileWeb3SDK not initialized. Call MobileWeb3SDK.init() first."
            )
        }

        fun getInstanceOrNull(): MobileWeb3SDK? = instance
    }
}
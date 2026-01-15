package com.mobileweb3.sdk

import android.content.Context
import com.mobileweb3.contracts.AccessResult
import com.mobileweb3.contracts.ContractsModule
import com.mobileweb3.contracts.TokenType
import com.mobileweb3.core.AppMetadata
import com.mobileweb3.core.Chain
import com.mobileweb3.core.RpcProvider
import com.mobileweb3.core.Web3Config
import com.mobileweb3.wallet.ConnectResult
import com.mobileweb3.wallet.ConnectedWallet
import com.mobileweb3.wallet.WalletModule
import com.mobileweb3.wallet.WalletState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigInteger

/**
 * Exceção lançada quando não há wallet conectada
 */
class NotConnectedException : Exception("Wallet não conectada. Chame connect() primeiro.")

/**
 * Facade principal do Mobile Web3 SDK
 * 
 * Este é o ponto de entrada único para toda a funcionalidade do SDK.
 * Ele agrega os módulos core, contracts e wallet em uma API simples.
 * 
 * Uso:
 * ```kotlin
 * // Inicialização (no Application.onCreate)
 * val sdk = MobileWeb3SDK.init(context) {
 *     chain = Chain.PolygonAmoy
 *     projectId = "seu-walletconnect-project-id"
 *     appMetadata {
 *         name = "Meu App"
 *         url = "https://meuapp.com"
 *     }
 * }
 * 
 * // Conectar wallet
 * sdk.setWalletForTesting("0x...") // Para demo
 * // ou sdk.connect() // Para produção
 * 
 * // Verificar token gating
 * val hasAccess = sdk.checkAccess(tokenContract = "0x...")
 * ```
 */
class MobileWeb3SDK private constructor(
    private val context: Context,
    val config: Web3Config
) {
    // ==================== MÓDULOS INTERNOS ====================
    
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

    /** Estado atual da wallet (observe via StateFlow) */
    val walletState: StateFlow<WalletState>
        get() = walletModule.state

    /** Verifica se há wallet conectada */
    val isConnected: Boolean
        get() = walletModule.isConnected

    /** Wallet conectada atual (ou null) */
    val currentWallet: ConnectedWallet?
        get() = walletModule.currentWallet

    /** Endereço da wallet conectada (ou null) */
    val walletAddress: String?
        get() = walletModule.address

    /** Inicializa WalletConnect (chamar no Application.onCreate se usar conexão real) */
    fun initializeWallet() {
        walletModule.initialize()
    }

    /** Conecta com wallet via WalletConnect */
    suspend fun connect(timeoutSeconds: Long = 60): ConnectResult {
        return walletModule.connect(timeoutSeconds)
    }

    /** Desconecta a wallet atual */
    fun disconnect() {
        walletModule.disconnect()
    }

    /** 
     * Define wallet manualmente (para testes/demo)
     * Útil para demonstrar funcionalidade sem integração WalletConnect completa
     */
    fun setWalletForTesting(address: String) {
        walletModule.setConnectedWallet(address, config.chain.chainId)
    }

    // ==================== TOKEN GATING ====================

    /** 
     * Verifica acesso simples (true/false)
     * Usa a wallet conectada automaticamente
     * 
     * @param tokenContract Endereço do contrato do token
     * @param minBalance Quantidade mínima de tokens necessária (default: 1)
     * @param tokenType Tipo do token: ERC20 ou ERC721 (default: ERC20)
     * @return true se o usuário tem tokens suficientes
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
     * Retorna informações sobre balance atual vs necessário
     * 
     * @return AccessResult.Granted, AccessResult.Denied ou AccessResult.Error
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

    /**
     * Verifica acesso para um endereço específico
     * Útil quando você quer verificar um endereço diferente da wallet conectada
     */
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

    /**
     * Obtém o balance nativo (POL/ETH) de um endereço
     */
    suspend fun getNativeBalance(address: String): BigInteger {
        val result = rpcProvider.call(
            method = "eth_getBalance",
            params = listOf(address, "latest")
        )
        val hex = result.jsonPrimitive.content.removePrefix("0x")
        return if (hex.isEmpty()) BigInteger.ZERO else BigInteger(hex, 16)
    }

    // ==================== ACESSO DIRETO AOS MÓDULOS ====================

    /** Acesso ao módulo de contratos para operações avançadas */
    val contracts: ContractsModule
        get() = contractsModule

    /** Acesso ao módulo de wallet para operações avançadas */
    val wallet: WalletModule
        get() = walletModule

    // ==================== UTILIDADES ====================

    /** Chain atual configurada */
    val chain: Chain
        get() = config.chain

    /** Verifica conexão com a blockchain */
    suspend fun checkBlockchainConnection(): Result<Int> = runCatching {
        rpcProvider.getChainId()
    }

    // ==================== COMPANION OBJECT (SINGLETON) ====================
    
    companion object {
        @Volatile
        private var instance: MobileWeb3SDK? = null

        /**
         * Inicializa o SDK com as configurações fornecidas
         * 
         * @param context Context do Android (Application ou Activity)
         * @param block DSL para configuração do SDK
         * @return Instância do SDK
         */
        fun init(context: Context, block: Web3Config.Builder.() -> Unit): MobileWeb3SDK {
            val config = Web3Config.Builder().apply(block).build()
            return MobileWeb3SDK(context.applicationContext, config).also {
                instance = it
            }
        }

        /**
         * Retorna a instância atual do SDK
         * @throws IllegalStateException se o SDK não foi inicializado
         */
        fun getInstance(): MobileWeb3SDK {
            return instance ?: throw IllegalStateException(
                "MobileWeb3SDK not initialized. Call MobileWeb3SDK.init() first."
            )
        }

        /**
         * Retorna a instância atual ou null se não inicializado
         */
        fun getInstanceOrNull(): MobileWeb3SDK? = instance
    }
}

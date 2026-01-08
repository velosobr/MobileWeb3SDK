package com.mobileweb3.core

import android.content.Context

/**
 * Ponto de entrada principal do SDK
 */
class MobileWeb3 private constructor(
    val config: Web3Config,
    val rpcProvider: RpcProvider
) {
    companion object {
        @Volatile
        private var instance: MobileWeb3? = null

        /**
         * Inicializa o SDK com configuração via DSL
         *
         * Exemplo:
         * ```
         * val sdk = MobileWeb3.init(context) {
         *     chain = Chain.PolygonMumbai
         *     projectId = "seu-project-id"
         *     appMetadata {
         *         name = "Meu App"
         *         url = "https://meuapp.com"
         *     }
         * }
         * ```
         */
        fun init(
            context: Context,
            block: Web3Config.Builder.() -> Unit
        ): MobileWeb3 {
            val config = Web3Config.Builder().apply(block).build()

            val rpcProvider = RpcProvider(
                rpcUrl = config.effectiveRpcUrl,
                timeout = config.requestTimeout.inWholeSeconds,
                enableLogging = config.enableLogging
            )

            return MobileWeb3(config, rpcProvider).also {
                instance = it
            }
        }

        /**
         * Retorna a instância atual do SDK
         * @throws IllegalStateException se não inicializado
         */
        fun getInstance(): MobileWeb3 {
            return instance ?: throw IllegalStateException(
                "MobileWeb3 not initialized. Call MobileWeb3.init() first."
            )
        }

        /**
         * Retorna a instância atual ou null se não inicializado
         */
        fun getInstanceOrNull(): MobileWeb3? = instance
    }

    /** Chain configurada */
    val chain: Chain get() = config.chain

    /** Project ID do WalletConnect */
    val projectId: String get() = config.projectId

    /**
     * Verifica conectividade com a blockchain
     */
    suspend fun checkConnection(): Result<Int> = runCatching {
        rpcProvider.getChainId()
    }
}

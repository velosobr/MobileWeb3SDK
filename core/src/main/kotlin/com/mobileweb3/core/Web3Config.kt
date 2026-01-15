package com.mobileweb3.core

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Configuração do SDK
 */
data class Web3Config(
    val chain: Chain,
    val projectId: String,
    val appMetadata: AppMetadata,
    val rpcUrl: String? = null,
    val requestTimeout: Duration = 30.seconds,
    val enableLogging: Boolean = false
) {
    /** RPC URL efetivo (custom ou padrão da chain) */
    val effectiveRpcUrl: String
        get() = rpcUrl ?: chain.rpcUrl

    class Builder {
        var chain: Chain = Chain.PolygonAmoy  // <-- AQUI
        var projectId: String = ""
        var rpcUrl: String? = null
        var requestTimeout: Duration = 30.seconds
        var enableLogging: Boolean = false

        private var appMetadataBuilder = AppMetadata.Builder()

        fun appMetadata(block: AppMetadata.Builder.() -> Unit) {
            appMetadataBuilder.apply(block)
        }

        fun build(): Web3Config {
            require(projectId.isNotBlank()) { "WalletConnect projectId is required" }

            return Web3Config(
                chain = chain,
                projectId = projectId,
                appMetadata = appMetadataBuilder.build(),
                rpcUrl = rpcUrl,
                requestTimeout = requestTimeout,
                enableLogging = enableLogging
            )
        }
    }
}
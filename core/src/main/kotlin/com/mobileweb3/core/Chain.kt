package com.mobileweb3.core

/**
 * Redes blockchain suportadas pelo SDK
 */
sealed class Chain(
    val chainId: Int,
    val name: String,
    val rpcUrl: String,
    val explorerUrl: String,
    val currencySymbol: String
) {
    /** Polygon Mainnet */
    data object Polygon : Chain(
        chainId = 137,
        name = "Polygon",
        rpcUrl = "https://polygon-rpc.com",
        explorerUrl = "https://polygonscan.com",
        currencySymbol = "MATIC"
    )

    /** Polygon Mumbai Testnet */
    data object PolygonMumbai : Chain(
        chainId = 80001,
        name = "Polygon Mumbai",
        rpcUrl = "https://rpc-mumbai.maticvigil.com",
        explorerUrl = "https://mumbai.polygonscan.com",
        currencySymbol = "MATIC"
    )

    companion object {
        /** Retorna Chain pelo chainId */
        fun fromChainId(chainId: Int): Chain? = when (chainId) {
            137 -> Polygon
            80001 -> PolygonMumbai
            else -> null
        }
    }
}
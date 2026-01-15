package com.mobileweb3.core

/**
 * Redes blockchain suportadas pelo SDK
 * 
 * Cada chain tem suas próprias configurações:
 * - chainId: identificador único da rede
 * - rpcUrl: endpoint para comunicação com a blockchain
 * - explorerUrl: site para visualizar transações
 * - currencySymbol: símbolo da moeda nativa
 */
sealed class Chain(
    val chainId: Int,
    val name: String,
    val rpcUrl: String,
    val explorerUrl: String,
    val currencySymbol: String
) {
    /** Polygon Mainnet - rede de produção */
    data object Polygon : Chain(
        chainId = 137,
        name = "Polygon",
        rpcUrl = "https://polygon-rpc.com",
        explorerUrl = "https://polygonscan.com",
        currencySymbol = "POL"
    )

    /** 
     * Polygon Amoy Testnet - rede de testes
     * Substitui a antiga Mumbai (descontinuada em 2024)
     */
    data object PolygonAmoy : Chain(
        chainId = 80002,
        name = "Polygon Amoy",
        rpcUrl = "https://rpc-amoy.polygon.technology",
        explorerUrl = "https://amoy.polygonscan.com",
        currencySymbol = "POL"
    )

    companion object {
        /** Retorna Chain pelo chainId */
        fun fromChainId(chainId: Int): Chain? = when (chainId) {
            137 -> Polygon
            80002 -> PolygonAmoy
            else -> null
        }
    }
}

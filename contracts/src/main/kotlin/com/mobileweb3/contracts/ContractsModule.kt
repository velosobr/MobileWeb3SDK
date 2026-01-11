package com.mobileweb3.contracts

import com.mobileweb3.core.RpcProvider

/**
 * Ponto de entrada do módulo Contracts
 * Expõe factories para criar instâncias de contratos
 */
class ContractsModule(
    private val rpcProvider: RpcProvider
) {
    private val reader = ContractReader(rpcProvider)

    /**
     * Token Gating engine
     */
    val tokenGating: TokenGating by lazy { TokenGating(reader) }

    /**
     * Cria instância para interagir com token ERC-20
     */
    fun erc20(contractAddress: String): Erc20Contract {
        return Erc20Contract(contractAddress, reader)
    }

    /**
     * Cria instância para interagir com NFT ERC-721
     */
    fun erc721(contractAddress: String): Erc721Contract {
        return Erc721Contract(contractAddress, reader)
    }

    /**
     * Acesso ao reader genérico para chamadas customizadas
     */
    fun reader(): ContractReader = reader
}
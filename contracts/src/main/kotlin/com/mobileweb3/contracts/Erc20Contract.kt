package com.mobileweb3.contracts

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Abstração para tokens ERC-20
 */
class Erc20Contract(
    private val contractAddress: String,
    private val reader: ContractReader
) {
    /**
     * Retorna o nome do token
     */
    suspend fun name(): String {
        return reader.readString(contractAddress, "name()")
    }

    /**
     * Retorna o símbolo do token
     */
    suspend fun symbol(): String {
        return reader.readString(contractAddress, "symbol()")
    }

    /**
     * Retorna o número de casas decimais
     */
    suspend fun decimals(): Int {
        return reader.readUint256(contractAddress, "decimals()").toInt()
    }

    /**
     * Retorna o supply total
     */
    suspend fun totalSupply(): BigInteger {
        return reader.readUint256(contractAddress, "totalSupply()")
    }

    /**
     * Retorna o balance de um endereço (em wei/menor unidade)
     */
    suspend fun balanceOf(address: String): BigInteger {
        val encodedAddress = AbiEncoder.encodeAddress(address)
        return reader.readUint256(contractAddress, "balanceOf(address)", encodedAddress)
    }

    /**
     * Retorna o balance formatado com decimais
     */
    suspend fun balanceOfFormatted(address: String): BigDecimal {
        val balance = balanceOf(address)
        val decimals = decimals()
        return BigDecimal(balance).divide(BigDecimal.TEN.pow(decimals))
    }

    /**
     * Retorna informações completas do token
     */
    suspend fun getTokenInfo(): TokenInfo {
        return TokenInfo(
            address = contractAddress,
            name = name(),
            symbol = symbol(),
            decimals = decimals()
        )
    }
}

/**
 * Informações do token
 */
data class TokenInfo(
    val address: String,
    val name: String,
    val symbol: String,
    val decimals: Int
)
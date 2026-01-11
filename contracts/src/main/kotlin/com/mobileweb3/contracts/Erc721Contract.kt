package com.mobileweb3.contracts

import java.math.BigInteger

/**
 * Abstração para NFTs ERC-721
 */
class Erc721Contract(
    private val contractAddress: String,
    private val reader: ContractReader
) {
    /**
     * Retorna o nome da coleção
     */
    suspend fun name(): String {
        return reader.readString(contractAddress, "name()")
    }

    /**
     * Retorna o símbolo da coleção
     */
    suspend fun symbol(): String {
        return reader.readString(contractAddress, "symbol()")
    }

    /**
     * Retorna quantos NFTs o endereço possui
     */
    suspend fun balanceOf(address: String): BigInteger {
        val encodedAddress = AbiEncoder.encodeAddress(address)
        return reader.readUint256(contractAddress, "balanceOf(address)", encodedAddress)
    }

    /**
     * Retorna o dono de um token específico
     */
    suspend fun ownerOf(tokenId: BigInteger): String {
        val encodedTokenId = AbiEncoder.encodeUint256(tokenId)
        return reader.readAddress(contractAddress, "ownerOf(uint256)", encodedTokenId)
    }

    /**
     * Verifica se endereço possui um token específico
     */
    suspend fun isOwnerOf(address: String, tokenId: BigInteger): Boolean {
        return try {
            val owner = ownerOf(tokenId)
            owner.equals(address, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Retorna informações da coleção
     */
    suspend fun getCollectionInfo(): CollectionInfo {
        return CollectionInfo(
            address = contractAddress,
            name = name(),
            symbol = symbol()
        )
    }
}

/**
 * Informações da coleção NFT
 */
data class CollectionInfo(
    val address: String,
    val name: String,
    val symbol: String
)
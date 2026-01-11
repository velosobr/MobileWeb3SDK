package com.mobileweb3.contracts

import java.math.BigInteger

/**
 * Resultado da verificação de acesso
 */
sealed class AccessResult {
    /**
     * Acesso liberado
     */
    data class Granted(
        val currentBalance: BigInteger,
        val requiredBalance: BigInteger
    ) : AccessResult()

    /**
     * Acesso negado
     */
    data class Denied(
        val currentBalance: BigInteger,
        val requiredBalance: BigInteger
    ) : AccessResult() {
        val missingAmount: BigInteger
            get() = requiredBalance - currentBalance
    }

    /**
     * Erro na verificação
     */
    data class Error(
        val exception: Exception
    ) : AccessResult()
}

/**
 * Requisito de token para acesso
 */
data class TokenRequirement(
    val contractAddress: String,
    val minBalance: BigInteger = BigInteger.ONE,
    val tokenType: TokenType = TokenType.ERC20
)

enum class TokenType {
    ERC20,
    ERC721
}

/**
 * Motor de Token Gating
 */
class TokenGating(
    private val reader: ContractReader
) {
    /**
     * Verifica se wallet tem acesso (verificação simples)
     */
    suspend fun checkAccess(
        wallet: String,
        tokenContract: String,
        minBalance: BigInteger = BigInteger.ONE,
        tokenType: TokenType = TokenType.ERC20
    ): Boolean {
        return when (val result = verifyAccess(wallet, tokenContract, minBalance, tokenType)) {
            is AccessResult.Granted -> true
            is AccessResult.Denied -> false
            is AccessResult.Error -> false
        }
    }

    /**
     * Verifica acesso com detalhes completos
     */
    suspend fun verifyAccess(
        wallet: String,
        tokenContract: String,
        minBalance: BigInteger = BigInteger.ONE,
        tokenType: TokenType = TokenType.ERC20
    ): AccessResult {
        return try {
            val balance = when (tokenType) {
                TokenType.ERC20 -> Erc20Contract(tokenContract, reader).balanceOf(wallet)
                TokenType.ERC721 -> Erc721Contract(tokenContract, reader).balanceOf(wallet)
            }

            if (balance >= minBalance) {
                AccessResult.Granted(
                    currentBalance = balance,
                    requiredBalance = minBalance
                )
            } else {
                AccessResult.Denied(
                    currentBalance = balance,
                    requiredBalance = minBalance
                )
            }
        } catch (e: Exception) {
            AccessResult.Error(e)
        }
    }

    /**
     * Verifica múltiplos tokens (lógica OR - precisa ter pelo menos um)
     */
    suspend fun checkAccessAny(
        wallet: String,
        requirements: List<TokenRequirement>
    ): Boolean {
        return requirements.any { req ->
            checkAccess(wallet, req.contractAddress, req.minBalance, req.tokenType)
        }
    }

    /**
     * Verifica múltiplos tokens (lógica AND - precisa ter todos)
     */
    suspend fun checkAccessAll(
        wallet: String,
        requirements: List<TokenRequirement>
    ): Boolean {
        return requirements.all { req ->
            checkAccess(wallet, req.contractAddress, req.minBalance, req.tokenType)
        }
    }

    /**
     * Verifica múltiplos tokens e retorna detalhes de cada
     */
    suspend fun verifyAccessAll(
        wallet: String,
        requirements: List<TokenRequirement>
    ): Map<String, AccessResult> {
        return requirements.associate { req ->
            req.contractAddress to verifyAccess(
                wallet,
                req.contractAddress,
                req.minBalance,
                req.tokenType
            )
        }
    }
}
package com.mobileweb3.contracts

import java.math.BigInteger

/**
 * Encoder básico para chamadas de contrato
 * Segue a especificação ABI do Ethereum
 */
object AbiEncoder {

    /**
     * Codifica function selector (primeiros 4 bytes do keccak256 da assinatura)
     * Ex: "balanceOf(address)" -> "0x70a08231"
     */
    fun encodeFunctionSelector(signature: String): String {
        val hash = org.web3j.crypto.Hash.sha3String(signature)
        return hash.substring(0, 10) // 0x + 8 chars = 4 bytes
    }

    /**
     * Codifica um endereço (20 bytes padded para 32 bytes)
     */
    fun encodeAddress(address: String): String {
        val clean = address.lowercase().removePrefix("0x")
        return clean.padStart(64, '0')
    }

    /**
     * Codifica um uint256
     */
    fun encodeUint256(value: BigInteger): String {
        return value.toString(16).padStart(64, '0')
    }

    /**
     * Monta chamada completa: selector + params
     */
    fun encodeCall(functionSignature: String, vararg params: String): String {
        val selector = encodeFunctionSelector(functionSignature)
        return selector + params.joinToString("")
    }

    /**
     * Decodifica resposta uint256
     */
    fun decodeUint256(hex: String): BigInteger {
        val clean = hex.removePrefix("0x")
        if (clean.isEmpty() || clean == "0".repeat(64)) {
            return BigInteger.ZERO
        }
        return BigInteger(clean, 16)
    }

    /**
     * Decodifica resposta string (dinâmico)
     */
    fun decodeString(hex: String): String {
        val clean = hex.removePrefix("0x")
        if (clean.length < 128) return ""
        
        // Offset (32 bytes) + Length (32 bytes) + Data
        val length = BigInteger(clean.substring(64, 128), 16).toInt()
        val dataHex = clean.substring(128, 128 + length * 2)
        
        return dataHex.chunked(2)
            .map { it.toInt(16).toChar() }
            .joinToString("")
    }

    /**
     * Decodifica resposta address
     */
    fun decodeAddress(hex: String): String {
        val clean = hex.removePrefix("0x")
        return "0x${clean.takeLast(40)}"
    }
}
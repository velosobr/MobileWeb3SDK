package com.mobileweb3.utils

import java.math.BigInteger

/**
 * Utilitários para conversão hexadecimal
 */
object HexUtils {

    /**
     * Converte ByteArray para hex string
     */
    fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }

    /**
     * Converte hex string para ByteArray
     */
    fun String.hexToByteArray(): ByteArray {
        val hex = removePrefix("0x")
        return ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }

    /**
     * Converte BigInteger para hex string com prefixo 0x
     */
    fun BigInteger.toHex(): String {
        return "0x${toString(16)}"
    }

    /**
     * Converte hex string para BigInteger
     */
    fun String.hexToBigInteger(): BigInteger {
        return BigInteger(removePrefix("0x"), 16)
    }

    /**
     * Adiciona padding à esquerda para completar 32 bytes (64 chars)
     */
    fun String.padLeftTo64(): String {
        val hex = removePrefix("0x")
        return hex.padStart(64, '0')
    }
}
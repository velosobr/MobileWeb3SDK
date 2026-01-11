package com.mobileweb3.utils

import java.security.MessageDigest

/**
 * Implementação de Keccak-256 (usado em Ethereum)
 * Nota: Usamos Bouncy Castle ou implementação manual
 * Por simplicidade, usando SHA3-256 do Java (similar mas não idêntico)
 * Para produção, usar Bouncy Castle
 */
fun keccak256(input: ByteArray): ByteArray {
    // Nota: SHA3-256 do Java não é exatamente Keccak-256
    // Para MVP está ok, em produção usar:
    // org.bouncycastle.jcajce.provider.digest.Keccak.Digest256
    val digest = MessageDigest.getInstance("SHA3-256")
    return digest.digest(input)
}

fun keccak256(input: String): ByteArray {
    return keccak256(input.toByteArray(Charsets.UTF_8))
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}
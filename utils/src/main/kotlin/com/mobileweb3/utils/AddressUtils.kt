package com.mobileweb3.utils

/**
 * Utilitários para manipulação de endereços Ethereum
 */
object AddressUtils {

    private val ADDRESS_REGEX = "^0x[a-fA-F0-9]{40}$".toRegex()

    /**
     * Valida se é um endereço Ethereum válido
     */
    fun isValid(address: String): Boolean {
        return ADDRESS_REGEX.matches(address)
    }

    /**
     * Formata endereço para exibição (0x1234...5678)
     */
    fun format(address: String, prefixLength: Int = 6, suffixLength: Int = 4): String {
        if (!isValid(address)) return address
        if (address.length <= prefixLength + suffixLength) return address

        return "${address.take(prefixLength)}...${address.takeLast(suffixLength)}"
    }

    /**
     * Normaliza endereço para lowercase
     */
    fun normalize(address: String): String {
        return address.lowercase()
    }

    /**
     * Aplica checksum EIP-55 ao endereço
     */
    fun toChecksumAddress(address: String): String {
        if (!isValid(address)) return address

        val lowercaseAddress = address.lowercase().removePrefix("0x")
        val hash = Keccak256.hash(lowercaseAddress.toByteArray())

        val checksumAddress = StringBuilder("0x")
        for (i in lowercaseAddress.indices) {
            val char = lowercaseAddress[i]
            if (char in '0'..'9') {
                checksumAddress.append(char)
            } else {
                val hashChar = hash[i].digitToInt(16)
                checksumAddress.append(if (hashChar >= 8) char.uppercaseChar() else char)
            }
        }

        return checksumAddress.toString()
    }
}
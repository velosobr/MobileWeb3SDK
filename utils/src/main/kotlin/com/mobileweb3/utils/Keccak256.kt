package com.mobileweb3.utils

/**
 * Implementação de Keccak-256 para Android
 * (SHA3-256 não está disponível nativamente no Android)
 */
object Keccak256 {

    private val RC = arrayOf(
        "0000000000000001", "0000000000008082", "800000000000808a",
        "8000000080008000", "000000000000808b", "0000000080000001",
        "8000000080008081", "8000000000008009", "000000000000008a",
        "0000000000000088", "0000000080008009", "000000008000000a",
        "000000008000808b", "800000000000008b", "8000000000008089",
        "8000000000008003", "8000000000008002", "8000000000000080",
        "000000000000800a", "800000008000000a", "8000000080008081",
        "8000000000008080", "0000000080000001", "8000000080008008"
    ).map { java.lang.Long.parseUnsignedLong(it, 16) }.toLongArray()

    private val R = intArrayOf(
        1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14,
        27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
    )

    private val PILN = intArrayOf(
        10, 7, 11, 17, 18, 3, 5, 16, 8, 21, 24, 4,
        15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    )

    fun hash(input: String): String {
        return hash(input.toByteArray(Charsets.UTF_8))
    }

    fun hash(input: ByteArray): String {
        return hashBytes(input).toHexString()
    }

    fun hashBytes(input: ByteArray): ByteArray {
        val rate = 136 // (1600 - 256 * 2) / 8
        val outputLen = 32 // 256 bits

        // Padding
        val padded = pad(input, rate)

        // Initialize state
        val state = LongArray(25)

        // Absorb
        for (i in padded.indices step rate) {
            for (j in 0 until rate / 8) {
                state[j] = state[j] xor bytesToLong(padded, i + j * 8)
            }
            keccakF(state)
        }

        // Squeeze
        val output = ByteArray(outputLen)
        for (i in 0 until outputLen / 8) {
            longToBytes(state[i], output, i * 8)
        }

        return output
    }

    private fun pad(input: ByteArray, rate: Int): ByteArray {
        val padLen = rate - (input.size % rate)
        val padded = ByteArray(input.size + padLen)
        System.arraycopy(input, 0, padded, 0, input.size)

        // Keccak padding: 0x01 ... 0x80
        padded[input.size] = 0x01
        padded[padded.size - 1] = (padded[padded.size - 1].toInt() or 0x80).toByte()

        return padded
    }

    private fun keccakF(state: LongArray) {
        val bc = LongArray(5)

        for (round in 0 until 24) {
            // Theta
            for (i in 0 until 5) {
                bc[i] = state[i] xor state[i + 5] xor state[i + 10] xor state[i + 15] xor state[i + 20]
            }
            for (i in 0 until 5) {
                val t = bc[(i + 4) % 5] xor rotateLeft(bc[(i + 1) % 5], 1)
                for (j in 0 until 25 step 5) {
                    state[j + i] = state[j + i] xor t
                }
            }

            // Rho and Pi
            var t = state[1]
            for (i in 0 until 24) {
                val j = PILN[i]
                bc[0] = state[j]
                state[j] = rotateLeft(t, R[i])
                t = bc[0]
            }

            // Chi
            for (j in 0 until 25 step 5) {
                for (i in 0 until 5) {
                    bc[i] = state[j + i]
                }
                for (i in 0 until 5) {
                    state[j + i] = state[j + i] xor (bc[(i + 1) % 5].inv() and bc[(i + 2) % 5])
                }
            }

            // Iota
            state[0] = state[0] xor RC[round]
        }
    }

    private fun rotateLeft(x: Long, n: Int): Long {
        return (x shl n) or (x ushr (64 - n))
    }

    private fun bytesToLong(b: ByteArray, offset: Int): Long {
        var result = 0L
        for (i in 0 until 8) {
            result = result or ((b[offset + i].toLong() and 0xFF) shl (i * 8))
        }
        return result
    }

    private fun longToBytes(v: Long, b: ByteArray, offset: Int) {
        for (i in 0 until 8) {
            b[offset + i] = ((v shr (i * 8)) and 0xFF).toByte()
        }
    }
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}
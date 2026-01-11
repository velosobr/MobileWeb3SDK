package com.mobileweb3.contracts

import com.mobileweb3.core.ContractCallException
import com.mobileweb3.core.InvalidAddressException
import com.mobileweb3.core.RpcProvider
import com.mobileweb3.utils.AddressUtils
import java.math.BigInteger

/**
 * Leitor genérico de contratos
 */
class ContractReader(
    private val rpcProvider: RpcProvider
) {
    /**
     * Executa chamada de leitura em contrato
     */
    suspend fun call(
        contractAddress: String,
        functionSignature: String,
        vararg params: String
    ): String {
        if (!AddressUtils.isValid(contractAddress)) {
            throw InvalidAddressException(contractAddress)
        }

        val data = AbiEncoder.encodeCall(functionSignature, *params)
        
        return try {
            rpcProvider.ethCall(contractAddress, data)
        } catch (e: Exception) {
            throw ContractCallException("Failed to call $functionSignature", e)
        }
    }

    /**
     * Lê um uint256 do contrato
     */
    suspend fun readUint256(
        contractAddress: String,
        functionSignature: String,
        vararg params: String
    ): BigInteger {
        val result = call(contractAddress, functionSignature, *params)
        return AbiEncoder.decodeUint256(result)
    }

    /**
     * Lê uma string do contrato
     */
    suspend fun readString(
        contractAddress: String,
        functionSignature: String,
        vararg params: String
    ): String {
        val result = call(contractAddress, functionSignature, *params)
        return AbiEncoder.decodeString(result)
    }

    /**
     * Lê um address do contrato
     */
    suspend fun readAddress(
        contractAddress: String,
        functionSignature: String,
        vararg params: String
    ): String {
        val result = call(contractAddress, functionSignature, *params)
        return AbiEncoder.decodeAddress(result)
    }
}
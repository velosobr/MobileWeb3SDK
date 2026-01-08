package com.mobileweb3.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Provider para chamadas JSON-RPC à blockchain
 */
class RpcProvider(
    private val rpcUrl: String,
    private val timeout: Long = 30L,
    private val enableLogging: Boolean = false
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .build()

    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }

    private val requestId = AtomicLong(1)

    /**
     * Executa uma chamada RPC
     */
    suspend fun call(
        method: String,
        params: List<Any> = emptyList()
    ): JsonElement = withContext(Dispatchers.IO) {
        val id = requestId.getAndIncrement()
        
        val requestBody = buildJsonObject {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", buildJsonArray {
                params.forEach { param ->
                    when (param) {
                        is String -> add(param)
                        is Number -> add(param)
                        is Boolean -> add(param)
                        is Map<*, *> -> add(buildJsonObject {
                            param.forEach { (k, v) ->
                                when (v) {
                                    is String -> put(k.toString(), v)
                                    is Number -> put(k.toString(), v)
                                    is Boolean -> put(k.toString(), v)
                                    else -> put(k.toString(), v.toString())
                                }
                            }
                        })
                        else -> add(param.toString())
                    }
                }
            })
            put("id", id)
        }

        if (enableLogging) {
            println("[RPC Request] $method: $requestBody")
        }

        val request = Request.Builder()
            .url(rpcUrl)
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() 
                ?: throw RpcException(-1, "Empty response")

            if (enableLogging) {
                println("[RPC Response] $responseBody")
            }

            val jsonResponse = json.parseToJsonElement(responseBody).jsonObject

            // Check for error
            jsonResponse["error"]?.let { error ->
                val errorObj = error.jsonObject
                val code = errorObj["code"]?.jsonPrimitive?.int ?: -1
                val message = errorObj["message"]?.jsonPrimitive?.content ?: "Unknown error"
                throw RpcException(code, message)
            }

            // Return result
            jsonResponse["result"] ?: throw RpcException(-1, "No result in response")

        } catch (e: IOException) {
            throw NoInternetException(cause = e)
        }
    }

    /**
     * eth_call - Chama função de contrato (read-only)
     */
    suspend fun ethCall(
        to: String,
        data: String,
        blockParameter: String = "latest"
    ): String {
        val result = call(
            method = "eth_call",
            params = listOf(
                mapOf("to" to to, "data" to data),
                blockParameter
            )
        )
        return result.jsonPrimitive.content
    }

    /**
     * eth_chainId - Retorna o chain ID
     */
    suspend fun getChainId(): Int {
        val result = call("eth_chainId")
        val hex = result.jsonPrimitive.content
        return hex.removePrefix("0x").toInt(16)
    }

    /**
     * eth_blockNumber - Retorna o número do bloco atual
     */
    suspend fun getBlockNumber(): Long {
        val result = call("eth_blockNumber")
        val hex = result.jsonPrimitive.content
        return hex.removePrefix("0x").toLong(16)
    }
}
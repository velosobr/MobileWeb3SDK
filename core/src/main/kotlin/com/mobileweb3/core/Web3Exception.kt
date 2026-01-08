package com.mobileweb3.core

/**
 * Hierarquia de exceções do SDK
 */
sealed class Web3Exception(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

// === Configuration Exceptions ===

sealed class ConfigurationException(
    message: String,
    cause: Throwable? = null
) : Web3Exception(message, cause)

class InvalidProjectIdException(
    message: String = "Invalid or missing WalletConnect Project ID"
) : ConfigurationException(message)

class InvalidRpcUrlException(
    url: String,
    cause: Throwable? = null
) : ConfigurationException("Invalid RPC URL: $url", cause)

class UnsupportedChainException(
    chainId: Int
) : ConfigurationException("Chain not supported: $chainId")

// === Connection Exceptions ===

sealed class ConnectionException(
    message: String,
    cause: Throwable? = null
) : Web3Exception(message, cause)

class WalletNotInstalledException(
    message: String = "No compatible wallet app installed"
) : ConnectionException(message)

class ConnectionTimeoutException(
    message: String = "Connection timed out"
) : ConnectionException(message)

class ConnectionRejectedException(
    message: String = "Connection rejected by user"
) : ConnectionException(message)

class SessionExpiredException(
    message: String = "Wallet session expired"
) : ConnectionException(message)

class NotConnectedException(
    message: String = "Wallet not connected"
) : ConnectionException(message)

// === Contract Exceptions ===

sealed class ContractException(
    message: String,
    cause: Throwable? = null
) : Web3Exception(message, cause)

class InvalidAddressException(
    address: String
) : ContractException("Invalid address: $address")

class ContractCallException(
    message: String,
    cause: Throwable? = null
) : ContractException(message, cause)

class AbiDecodingException(
    message: String,
    cause: Throwable? = null
) : ContractException(message, cause)

// === Network Exceptions ===

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Web3Exception(message, cause)

class RpcException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : NetworkException("RPC Error ($code): $message", cause)

class RequestTimeoutException(
    message: String = "Request timed out"
) : NetworkException(message)

class NoInternetException(
    message: String = "No internet connection",
    cause: Throwable? = null
) : NetworkException(message, cause)
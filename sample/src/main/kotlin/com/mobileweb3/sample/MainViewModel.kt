package com.mobileweb3.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileweb3.sdk.MobileWeb3SDK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

sealed class UiState {
    data object Connect : UiState()
    data object Connecting : UiState()
    data class Verifying(val address: String) : UiState()
    data class Granted(val address: String, val balance: String) : UiState()
    data class Denied(val address: String, val currentBalance: String, val requiredBalance: String) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Connect)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Requer pelo menos 0.01 POL para ter acesso
    private val requiredBalanceWei = BigInteger("10000000000000000") // 0.01 * 10^18

    fun connect() {
        viewModelScope.launch {
            _uiState.value = UiState.Connecting
            delay(1000)

            try {
                val sdk = MobileWeb3SDK.getInstance()

                // Seu endereço MetaMask
                val testAddress = "0x8435814c1aa03dfc0aab9856fe7d47931e2f454f"

                sdk.setWalletForTesting(testAddress)
                verifyNativeBalance(testAddress)

            } catch (e: Exception) {
                android.util.Log.e("TokenGate", "Erro ao conectar", e)
                _uiState.value = UiState.Error(e.message ?: "Erro ao conectar")
            }
        }
    }

    private suspend fun verifyNativeBalance(address: String) {
        _uiState.value = UiState.Verifying(address)
        delay(1500)

        try {
            val sdk = MobileWeb3SDK.getInstance()

            android.util.Log.d("TokenGate", "Verificando balance nativo para: $address")

            // Chama eth_getBalance via RPC
            val balanceWei = sdk.getNativeBalance(address)

            android.util.Log.d("TokenGate", "Balance em Wei: $balanceWei")

            if (balanceWei >= requiredBalanceWei) {
                _uiState.value = UiState.Granted(
                    address = address,
                    balance = formatPol(balanceWei)
                )
            } else {
                _uiState.value = UiState.Denied(
                    address = address,
                    currentBalance = formatPol(balanceWei),
                    requiredBalance = formatPol(requiredBalanceWei)
                )
            }

        } catch (e: Exception) {
            android.util.Log.e("TokenGate", "Erro na verificação", e)
            _uiState.value = UiState.Error(e.message ?: "Erro ao verificar")
        }
    }

    private fun formatPol(wei: BigInteger): String {
        val divisor = BigDecimal.TEN.pow(18)
        val pol = BigDecimal(wei).divide(divisor, 4, RoundingMode.DOWN)
        return "$pol POL"
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                MobileWeb3SDK.getInstance().disconnect()
            } catch (_: Exception) { }
            _uiState.value = UiState.Connect
        }
    }

    fun onBuyClick() { }

    fun retry() {
        _uiState.value = UiState.Connect
    }
}

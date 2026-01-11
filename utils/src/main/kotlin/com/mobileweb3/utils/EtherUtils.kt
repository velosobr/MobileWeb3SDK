package com.mobileweb3.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Unidades de Ether
 */
enum class EtherUnit(val decimals: Int) {
    WEI(0),
    GWEI(9),
    ETHER(18);

    val factor: BigInteger
        get() = BigInteger.TEN.pow(decimals)
}

/**
 * Utilitários para conversão de valores Ether/Wei
 */
object EtherUtils {

    /**
     * Converte valor para Wei
     * Ex: toWei(1.5, EtherUnit.ETHER) -> 1500000000000000000
     */
    fun toWei(value: BigDecimal, unit: EtherUnit): BigInteger {
        return value.multiply(BigDecimal(unit.factor)).toBigInteger()
    }

    fun toWei(value: Double, unit: EtherUnit): BigInteger {
        return toWei(BigDecimal.valueOf(value), unit)
    }

    fun toWei(value: Long, unit: EtherUnit): BigInteger {
        return BigInteger.valueOf(value).multiply(unit.factor)
    }

    /**
     * Converte Wei para unidade especificada
     * Ex: fromWei(1500000000000000000, EtherUnit.ETHER) -> 1.5
     */
    fun fromWei(value: BigInteger, unit: EtherUnit, scale: Int = 18): BigDecimal {
        return BigDecimal(value)
            .divide(BigDecimal(unit.factor), scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
    }

    /**
     * Formata Wei para string legível
     * Ex: formatWei(1500000000000000000) -> "1.5 ETH"
     */
    fun formatWei(
        value: BigInteger,
        unit: EtherUnit = EtherUnit.ETHER,
        decimals: Int = 4,
        symbol: String = "ETH"
    ): String {
        val converted = fromWei(value, unit, decimals)
        return "$converted $symbol"
    }

    /**
     * Converte valor com decimais customizados (para tokens ERC-20)
     */
    fun fromTokenUnits(value: BigInteger, decimals: Int, scale: Int = 18): BigDecimal {
        val factor = BigInteger.TEN.pow(decimals)
        return BigDecimal(value)
            .divide(BigDecimal(factor), scale, RoundingMode.HALF_UP)
            .stripTrailingZeros()
    }

    fun toTokenUnits(value: BigDecimal, decimals: Int): BigInteger {
        val factor = BigInteger.TEN.pow(decimals)
        return value.multiply(BigDecimal(factor)).toBigInteger()
    }
}
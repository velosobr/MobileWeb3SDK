package com.mobileweb3.core

/**
 * Metadados do app para exibição na wallet durante conexão
 */
data class AppMetadata(
    val name: String,
    val description: String,
    val url: String,
    val iconUrl: String
) {
    class Builder {
        var name: String = ""
        var description: String = ""
        var url: String = ""
        var iconUrl: String = ""

        fun build(): AppMetadata {
            require(name.isNotBlank()) { "App name is required" }
            require(url.isNotBlank()) { "App URL is required" }
            
            return AppMetadata(
                name = name,
                description = description,
                url = url,
                iconUrl = iconUrl
            )
        }
    }
}
package com.mobileweb3.sample

import android.app.Application
import com.mobileweb3.core.Chain
import com.mobileweb3.sdk.MobileWeb3SDK

/**
 * Application class - inicializa o SDK quando o app abre
 */
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o Mobile Web3 SDK
        MobileWeb3SDK.init(this) {
            // Rede: Polygon Amoy (testnet)
            chain = Chain.PolygonAmoy
            
            // WalletConnect Project ID
            projectId = "67848a989961a9173d9212ebf8aa3213"
            
            // Ativa logs para debug
            enableLogging = true
            
            // Metadados do app (exibidos na wallet)
            appMetadata {
                name = "Token Gate Demo"
                description = "Demo app for Mobile Web3 SDK"
                url = "https://mobileweb3sdk.dev"
                iconUrl = "https://avatars.githubusercontent.com/u/37784886"
            }
        }
    }
}

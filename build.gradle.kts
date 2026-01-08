// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}

subprojects {
    configurations.all {
        resolutionStrategy {
            // Force the newer Bouncy Castle version
            force("org.bouncycastle:bcprov-jdk18on:1.77")
        }
        // Exclude the older Bouncy Castle to prevent duplicate classes
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")

        // Exclude org.web3j modules - WalletConnect includes its own fork (com.walletconnect.web3j)
        exclude(group = "org.web3j", module = "abi")
        exclude(group = "org.web3j", module = "crypto")
        exclude(group = "org.web3j", module = "core")
        exclude(group = "org.web3j", module = "rlp")
        exclude(group = "org.web3j", module = "utils")
        exclude(group = "org.web3j", module = "tuples")
    }
}

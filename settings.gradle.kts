pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MobileWeb3SDK"

// Módulos da biblioteca
include(":utils")      // Base: helpers, conversões
include(":core")       // Fundação: config, RPC, tipos
include(":contracts")  // Contratos: ERC20, ERC721, Token Gating
include(":wallet")     // Wallet: WalletConnect
include(":sdk")        // Facade: agrega tudo em API simples

// App de demonstração
include(":sample")

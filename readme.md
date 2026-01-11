# Mobile Web3 SDK for Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square" alt="Language" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-blue?style=flat-square" alt="Min SDK" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License" />
</p>

<p align="center">
  <b>Simplify Web3 integration in your Android apps.</b><br/>
  Connect wallets, read smart contracts, and implement token gating — all with a few lines of Kotlin.
</p>

---

## 🎯 The Problem

Integrating Web3 into Android apps is painful:

- **Web3j is complex** — Heavy library with steep learning curve
- **Documentation is fragmented** — Scattered across multiple sources
- **Too much boilerplate** — Hundreds of lines for simple operations
- **Not Kotlin-friendly** — Most solutions are Java-first or generic wrappers

## ✨ The Solution

Mobile Web3 SDK abstracts blockchain complexity into simple, idiomatic Kotlin APIs.

```kotlin
// Initialize once
val sdk = MobileWeb3SDK.init(context) {
    chain = Chain.Polygon
    projectId = "your-walletconnect-id"
    appMetadata {
        name = "My App"
        url = "https://myapp.com"
    }
}

// Check token gating in 3 lines
val hasAccess = sdk.checkAccess(
    tokenContract = "0x...",
    minBalance = 1
)

if (hasAccess) showVipContent() else showPaywall()
```

**That's it.** No ABI parsing, no RPC configuration, no Web3 expertise required.

---

## 🚀 Features

| Feature | Status | Description |
|---------|--------|-------------|
| **Wallet Connection** | ✅ | WalletConnect v2 integration |
| **Token Gating** | ✅ | ERC-20 and ERC-721 support |
| **Contract Reading** | ✅ | Type-safe contract calls |
| **Multi-chain** | ✅ | Polygon, Mumbai testnet |
| **Kotlin-first** | ✅ | Coroutines, DSL builders |

---

## 📦 Installation

Add to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.user:mobile-web3-sdk:1.0.0")
}
```

---

## 🔧 Quick Start

### 1. Initialize the SDK

In your `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        MobileWeb3SDK.init(this) {
            chain = Chain.PolygonMumbai  // or Chain.Polygon for mainnet
            projectId = "your-walletconnect-project-id"
            enableLogging = BuildConfig.DEBUG
            
            appMetadata {
                name = "My App"
                description = "My awesome Web3 app"
                url = "https://myapp.com"
                iconUrl = "https://myapp.com/icon.png"
            }
        }
    }
}
```

### 2. Connect a Wallet

```kotlin
val sdk = MobileWeb3SDK.getInstance()

// Connect (opens MetaMask or other wallet)
val result = sdk.connect()

when (result) {
    is ConnectResult.Success -> {
        val address = result.wallet.address
        showConnectedUI(address)
    }
    is ConnectResult.Cancelled -> {
        showMessage("Connection cancelled")
    }
    is ConnectResult.Error -> {
        showError(result.message)
    }
}
```

### 3. Implement Token Gating

```kotlin
// Simple check (returns Boolean)
val hasAccess = sdk.checkAccess(
    tokenContract = "0xYourTokenAddress",
    minBalance = BigInteger.ONE
)

// Detailed check (returns balance info)
val result = sdk.verifyAccess(
    tokenContract = "0xYourTokenAddress",
    minBalance = BigInteger.ONE
)

when (result) {
    is AccessResult.Granted -> {
        // User has required tokens
        showVipContent()
    }
    is AccessResult.Denied -> {
        // User doesn't have enough tokens
        showPaywall(
            current = result.currentBalance,
            required = result.requiredBalance
        )
    }
    is AccessResult.Error -> {
        showError(result.exception.message)
    }
}
```

### 4. Read Token Data

```kotlin
// ERC-20 tokens
val token = sdk.contracts.erc20("0xTokenAddress")
val balance = token.balanceOf(walletAddress)
val symbol = token.symbol()
val decimals = token.decimals()

// ERC-721 NFTs
val nft = sdk.contracts.erc721("0xNftAddress")
val nftCount = nft.balanceOf(walletAddress)
val owner = nft.ownerOf(tokenId)
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                 Your Android App                 │
└─────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────┐
│              MobileWeb3SDK (Facade)              │
├─────────────────────────────────────────────────┤
│  ┌─────────┐  ┌──────────┐  ┌───────┐  ┌─────┐ │
│  │ Wallet  │  │ Contracts│  │ Core  │  │Utils│ │
│  │ Module  │  │  Module  │  │Module │  │     │ │
│  └─────────┘  └──────────┘  └───────┘  └─────┘ │
└─────────────────────────────────────────────────┘
                        │
          ┌─────────────┼─────────────┐
          ▼             ▼             ▼
   ┌────────────┐ ┌──────────┐ ┌──────────┐
   │WalletConnect│ │ Polygon  │ │  Web3j   │
   │     v2     │ │   RPC    │ │(minimal) │
   └────────────┘ └──────────┘ └──────────┘
```

### Modules

| Module | Purpose |
|--------|---------|
| `core` | SDK initialization, configuration, RPC provider |
| `wallet` | WalletConnect integration, session management |
| `contracts` | ERC-20, ERC-721, token gating logic |
| `utils` | Address formatting, unit conversions |

---

## 📱 Demo App

The repository includes a sample app demonstrating token gating:

```
sample/
├── ConnectScreen      → Wallet connection UI
├── VerifyingScreen    → Loading state
├── AccessGrantedScreen → VIP content (has token)
└── AccessDeniedScreen  → Paywall (no token)
```

### Running the Demo

1. Clone the repository
2. Open in Android Studio
3. Replace `YOUR_PROJECT_ID` in `SampleApplication.kt`
4. Run on device/emulator with MetaMask installed

---

## ⚙️ Configuration Options

```kotlin
MobileWeb3SDK.init(context) {
    // Required
    chain = Chain.Polygon           // Blockchain network
    projectId = "xxx"               // WalletConnect Cloud Project ID
    
    // Optional
    rpcUrl = "https://custom-rpc"   // Custom RPC endpoint
    requestTimeout = 30.seconds     // Request timeout
    enableLogging = true            // Debug logging
    
    // App metadata (shown in wallet)
    appMetadata {
        name = "App Name"           // Required
        url = "https://app.com"     // Required
        description = "..."         // Optional
        iconUrl = "https://..."     // Optional
    }
}
```

### Supported Chains

| Chain | Chain ID | Type |
|-------|----------|------|
| Polygon | 137 | Mainnet |
| Polygon Mumbai | 80001 | Testnet |

---

## 🔐 Token Gating Use Cases

- **Membership access** — Exclusive content for token holders
- **Premium features** — Unlock app features with tokens
- **NFT communities** — Verify NFT ownership
- **Early access** — Beta features for supporters
- **Loyalty programs** — Rewards based on token balance

---

## 🛠️ Requirements

- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+
- Java 17

---

## 📋 Roadmap

### v1.0 (Current)
- [x] Wallet connection via WalletConnect v2
- [x] ERC-20 token gating
- [x] ERC-721 NFT gating
- [x] Polygon support

### v1.1 (Planned)
- [ ] More chains (Ethereum, Base, Arbitrum)
- [ ] Transaction signing
- [ ] Message signing
- [ ] ENS resolution

### v2.0 (Future)
- [ ] iOS SDK (Swift)
- [ ] React Native wrapper
- [ ] Kotlin Multiplatform

---

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.

---

## 📄 License

```
MIT License

Copyright (c) 2026 Mobile Web3 SDK

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📬 Contact

- **Author:** Lino ([@velosobr](https://github.com/velosobr))
- **Project:** [github.com/velosobr/mobile-web3-sdk](https://github.com/velosobr/mobile-web3-sdk)

---

<p align="center">
  Built with ❤️ for the Web3 developer community
</p>

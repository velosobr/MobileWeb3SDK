KEY TECHNICAL DECISIONS
Decision 1: Custom Keccak-256 Implementation
What they might ask: "Why did you implement your own hashing algorithm?"
"Ethereum uses Keccak-256 for everything — function selectors, address checksums, event signatures. The problem is that Android doesn't have SHA3-256 in its standard library, and Keccak-256 is actually different from the standardized SHA3-256.
I initially tried using Java's MessageDigest with SHA3-256, but it threw NoSuchAlgorithmException on Android devices. The alternative was adding Bouncy Castle — a heavy cryptographic library.
Instead, I implemented Keccak-256 from scratch in pure Kotlin. It's about 100 lines of code, no external dependencies, and works on all Android versions. This keeps the SDK lightweight and self-contained."
Technical details if they dig deeper:
Keccak uses a sponge construction with 1600-bit state
Rate of 1088 bits (136 bytes) for Keccak-256
24 rounds of permutation with five operations: theta, rho, pi, chi, iota
Output is 256 bits (32 bytes)
Decision 2: JSON-RPC Communication
What they might ask: "How do you communicate with the blockchain?"
"Blockchains expose a JSON-RPC API. You send a JSON request, you get a JSON response. I built an RPC Provider class that handles this communication.
The main method is eth_call — it allows you to call smart contract functions without spending gas. You send the contract address and encoded function data, and the blockchain returns the result.
For example, to check someone's token balance, I send:
Method: eth_call
To: the token contract address
Data: the encoded balanceOf(address) function call
I used OkHttp for HTTP communication and Kotlin Serialization for JSON parsing. All calls are suspend functions using coroutines, so they're non-blocking and can be easily integrated with Android's lifecycle."
Code example you can reference:
suspend fun ethCall(to: String, data: String): String {
    val result = call(
        method = "eth_call",
        params = listOf(
            mapOf("to" to to, "data" to data),
            "latest"
        )
    )
    return result.jsonPrimitive.content
}
Decision 3: ABI Encoding
What they might ask: "How do you encode smart contract calls?"
"Smart contracts use the ABI — Application Binary Interface — to define how functions are called. When you call balanceOf(address), you need to encode it in a specific format.
First, you compute the function selector: take the Keccak-256 hash of the function signature and use the first 4 bytes. For balanceOf(address), that's 0x70a08231.
Then, you encode the parameters. Each parameter is padded to 32 bytes. An Ethereum address is 20 bytes, so you left-pad it with 12 bytes of zeros.
The final call data is: selector + encoded parameters.
I implemented this encoder manually rather than using Web3j because I only needed a subset of functionality, and keeping it lightweight was a priority."
Decision 4: Sealed Classes for State Management
What they might ask: "How do you handle different states and results?"
"I used Kotlin sealed classes extensively for type-safe state management. For example, the access verification result is:
sealed class AccessResult {
    data class Granted(val currentBalance: BigInteger, val requiredBalance: BigInteger)
    data class Denied(val currentBalance: BigInteger, val requiredBalance: BigInteger)
    data class Error(val exception: Throwable)
}
This forces consumers to handle all cases — the compiler ensures you don't forget the error case. It's much safer than returning nulls or throwing exceptions.
I applied the same pattern for wallet states: Disconnected, Connecting, Connected, Error. The UI can react to each state with a simple when expression."
Decision 5: Facade Pattern
What they might ask: "How do developers use your SDK?"
"I implemented the Facade pattern. Internally, there are multiple modules with their own classes and responsibilities. But developers only see one class: MobileWeb3SDK.
Initialization uses a DSL builder:
MobileWeb3SDK.init(context) {
    chain = Chain.PolygonAmoy
    projectId = "your-walletconnect-id"
    appMetadata {
        name = "My App"
        url = "https://myapp.com"
    }
}
Then all operations go through this facade:
val balance = sdk.getNativeBalance(address)
val hasAccess = sdk.checkAccess(tokenContract, minBalance)
This hides the complexity of RPC providers, contract readers, and ABI encoding. The developer doesn't need to know how it works internally."
Decision 6: Polygon Amoy Testnet
What they might ask: "Why Polygon instead of Ethereum?"
"For development and demos, Polygon is ideal. Transaction fees are fractions of a cent compared to dollars on Ethereum mainnet. The technology is identical — Polygon is EVM-compatible, so the same code works on both.
I specifically used Polygon Amoy, which is the testnet. It uses test POL tokens that you can get for free from faucets. This allows testing the entire flow without real money.
The architecture supports any EVM chain. Adding Ethereum mainnet or Arbitrum or Base is just configuration — changing the chain ID and RPC URL. No code changes required."

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mobileweb3.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // O módulo sdk agrega todos os outros
    api(project(":core"))
    api(project(":contracts"))
    api(project(":wallet"))
    api(project(":utils"))

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // JSON serialization (para acessar JsonElement do RpcProvider)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

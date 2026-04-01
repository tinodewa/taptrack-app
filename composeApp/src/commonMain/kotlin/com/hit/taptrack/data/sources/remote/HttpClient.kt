package com.hit.taptrack.data.sources.remote

import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// 1. Konfigurasi Ktor HttpClient
fun provideHttpClient(settings: Settings): HttpClient {
    return HttpClient {
        // Konfigurasi JSON Parser
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Penting: Abaikan field dari backend yang tidak ada di model aplikasi
            })
        }

        // Konfigurasi Logger (Sangat berguna untuk debugging)
        install(Logging) {
            logger = Logger.DEFAULT // Kamu bisa ganti dengan Napier nanti jika mau
            level = LogLevel.ALL
        }

        // Konfigurasi Default Request (Otomatis menyisipkan Token JWT)
        install(DefaultRequest) {
            header("Content-Type", "application/json")

            // Ambil token dari Multiplatform Settings
            val token = settings.getStringOrNull("JWT_TOKEN")
            if (!token.isNullOrEmpty()) {
                header("Authorization", "Bearer $token")
            }
        }
    }
}

// 2. Konfigurasi Ktorfit (Membungkus HttpClient)
fun provideKtorfit(httpClient: HttpClient): Ktorfit {
    // URL Backend Golang kamu.
    // CATATAN PENTING:
    // - Jika testing di Android Emulator, gunakan "http://10.0.2.2:8080/api/v1/"
    // - Jika testing di Desktop/iOS Simulator, gunakan "http://localhost:8080/api/v1/"
    // - Jika backend sudah di-deploy, gunakan URL server production-nya.
    val baseUrl = "https://taptrack-golang-refqti-2701ca-82-197-71-16.traefik.me/api/v1/" // Sesuaikan dengan port Golang kamu

    return Ktorfit.Builder()
        .baseUrl(baseUrl)
        .httpClient(httpClient)
        .build()
}
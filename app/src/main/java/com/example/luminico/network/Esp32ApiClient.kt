package com.example.luminico.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object Esp32ApiClient {

    private const val BASE_URL = "http://192.168.4.1"

    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/sensores")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 1500
                readTimeout = 1500
                requestMethod = "GET"
            }
            val responseCode = connection.responseCode
            connection.disconnect()
            ((responseCode in 200..299))
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getSensorsData(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/sensores")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2000
                readTimeout = 2000
                requestMethod = "GET"
            }
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()
            response
        } catch (_: Exception) {
            null
        }
    }

    suspend fun sendActuatorCommand(query: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/actuador?$query")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 2000
                readTimeout = 2000
                requestMethod = "POST"
                doOutput = true
            }
            val responseCode = connection.responseCode
            connection.disconnect()
            ((responseCode in 200..299))
        } catch (_: Exception) {
            false
        }
    }

    suspend fun changeWifiPassword(actualPass: String, newPass: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/password?actual=$actualPass&nueva=$newPass")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 3000
                readTimeout = 3000
                requestMethod = "POST"
                doOutput = true
            }
            val responseCode = connection.responseCode
            val response = try {
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (_: Exception) {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }
            connection.disconnect()
            Pair((responseCode in 200..299), response)
        } catch (e: Exception) {
            Pair(false, e.message ?: "Error de conexión")
        }
    }
}

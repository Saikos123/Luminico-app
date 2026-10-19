package com.example.luminico

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Esp32WifiManager {

    private val _connectionStatus = MutableStateFlow("Desconectado")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun bindToEsp32Wifi(context: Context, ssidName: String = "ESP32_Luminico_IoT") {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            unregisterCallback(connectivityManager)

            _connectionStatus.value = "Buscando $ssidName..."

            val specifier = WifiNetworkSpecifier.Builder()
                .setSsid(ssidName)
                .build()

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build()

            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    try {
                        connectivityManager.bindProcessToNetwork(network)
                        _connectionStatus.value = "Conectado a $ssidName"
                        Toast.makeText(context, "¡Conectado a la red del ESP32!", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) { }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    try {
                        connectivityManager.bindProcessToNetwork(null)
                    } catch (_: Exception) { }
                    _connectionStatus.value = "Red Perdida"
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    _connectionStatus.value = "Red no encontrada"
                    Toast.makeText(
                        context,
                        "No se encontró la red '$ssidName'. Conéctate manualmente en Ajustes de Wi-Fi.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            networkCallback?.let { connectivityManager.requestNetwork(request, it) }
        } catch (_: Exception) {
            _connectionStatus.value = "Conexión manual necesaria"
            Toast.makeText(
                context,
                "Ve a Ajustes de Wi-Fi y conéctate a '$ssidName'.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun unbindWifi(context: Context) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            unregisterCallback(connectivityManager)
            connectivityManager.bindProcessToNetwork(null)
            _connectionStatus.value = "Desconectado"
        } catch (_: Exception) { }
    }

    private fun unregisterCallback(connectivityManager: ConnectivityManager) {
        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (_: Exception) { }
            networkCallback = null
        }
    }
}

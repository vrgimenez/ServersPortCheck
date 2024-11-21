package ar.vrx_design.serversportcheck.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class PortCheckerViewModel : ViewModel() {

    // Cambiamos de String a un estado observable
    val portStatus = mutableStateOf("Esperando...")

    fun startChecking(host: String, port: Int, interval: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val status = if (checkPort(host, port)) {
                    "El puerto $port está abierto en $host"
                } else {
                    "El puerto $port está cerrado en $host"
                }
                portStatus.value = status // Actualizamos el estado observable
                delay(interval)
            }
        }
    }

    private fun checkPort(host: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 1000) // 1000 ms de tiempo de espera
                true
            }
        } catch (e: IOException) {
            false
        }
    }
}

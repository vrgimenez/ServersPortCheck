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

    val portStatus = mutableStateOf("Esperando...")

    fun startChecking(hostsAndPorts: List<Pair<String, Int>>, interval: Long = 10_000L) {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val statuses = hostsAndPorts.map { (host, port) ->
                    if (checkPort(host, port)) {
                        "El puerto $port está abierto en $host"
                    } else {
                        "El puerto $port está cerrado en $host"
                    }
                }
                portStatus.value = statuses.joinToString(separator = "\n")
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

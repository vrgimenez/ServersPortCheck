package ar.vrx_design.serversportcheck.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class PortCheckerViewModel : ViewModel() {

    val portStatuses = mutableStateOf(listOf<String>())
    private var isRunning = false
    private var checkingJob: Job? = null

    fun startChecking(hostsAndPorts: List<Pair<String, Int>>, interval: Long = 10_000L) {
        isRunning = true
        checkingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isRunning) {
                val statuses = hostsAndPorts.map { (host, port) ->
                    if (checkPort(host, port)) {
                        "Abierto"
                    } else {
                        "Cerrado"
                    }
                }
                portStatuses.value = statuses
                delay(interval)
            }
        }
    }

    fun stopChecking() {
        isRunning = false
        checkingJob?.cancel()
        portStatuses.value = listOf("Esperando...", "Esperando...")
    }

    private fun checkPort(host: String, port: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 1000)
                true
            }
        } catch (e: IOException) {
            false
        }
    }
}

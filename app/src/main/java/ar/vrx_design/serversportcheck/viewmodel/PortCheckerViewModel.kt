package ar.vrx_design.serversportcheck.viewmodel

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PortCheckerViewModel : ViewModel() {
    private val _portStatuses = MutableStateFlow<List<String>>(emptyList())
    val portStatuses: StateFlow<List<String>> get() = _portStatuses

    private val _rows = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val rows: StateFlow<List<Pair<String, String>>> get() = _rows

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> get() = _isChecking

    private var monitoringJob: Job? = null

    private var ringtone: Ringtone? = null

    fun startChecking(newRows: List<Pair<String, Int>>, context: Context) {
        _isChecking.value = true
        _portStatuses.value = List(newRows.size) { "Waiting" }

        monitoringJob = CoroutineScope(Dispatchers.IO).launch {
            while (_isChecking.value) {
                newRows.forEachIndexed { index, (host, port) ->
                    val status = checkPort(host, port)
                    _portStatuses.update { currentStatuses ->
                        currentStatuses.toMutableList().apply {
                            this[index] = status
                        }
                    }
                }
                // Reproducir sonido si algún estado es "Cerrado"
                if ("Cerrado" in _portStatuses.value) {
                    playAlarm(context)
                }
                delay(5000) // Esperar 5 segundos entre verificaciones
            }
        }
    }

    fun stopChecking() {
        _isChecking.value = false
        monitoringJob?.cancel()
        monitoringJob = null

        // Reiniciar los estados a "Waiting"
        _portStatuses.value = List(_rows.value.size) { "Waiting" }

        ringtone?.stop() // Detener sonido si está activo
    }

    fun updateRows(newRows: List<Pair<String, String>>) {
        _rows.value = newRows
        _portStatuses.value = List(newRows.size) { "Waiting" }
    }

    private fun playAlarm(context: Context) {
        if (ringtone == null) {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(context, notificationUri)
        }

        if (!ringtone!!.isPlaying) {
            ringtone?.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ringtone?.stop()
        ringtone = null
    }

    private fun checkPort(host: String, port: Int): String {
        // Lógica de verificación del puerto
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 2000)
                "Abierto"
            }
        } catch (e: Exception) {
            "Cerrado"
        }
    }
}

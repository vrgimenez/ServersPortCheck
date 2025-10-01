package ar.vrx_design.serversportcheck.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import ar.vrx_design.serversportcheck.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

class PortCheckerViewModel : ViewModel() {
    private val _portStatuses = MutableStateFlow<List<String>>(emptyList())
    val portStatuses: StateFlow<List<String>> get() = _portStatuses

    private val _rows = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val rows: StateFlow<List<Pair<String, String>>> get() = _rows

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> get() = _isChecking

    private var monitoringJob: Job? = null

    private var mediaPlayer: MediaPlayer? = null

    suspend fun exportToJson(context: Context): String {
        return withContext(Dispatchers.IO) {
            val jsonArray = JSONArray()
            rows.value.forEach { (host, port) ->
                val jsonObject = JSONObject()
                jsonObject.put("host", host)
                jsonObject.put("port", port)
                jsonArray.put(jsonObject)
            }
            jsonArray.toString()
        }
    }

    suspend fun importFromJson(context: Context, jsonString: String) {
        withContext(Dispatchers.IO) {
            val jsonArray = JSONArray(jsonString)
            val importedRows = mutableListOf<Pair<String, String>>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val host = jsonObject.getString("host")
                val port = jsonObject.getString("port")
                importedRows.add(host to port)
            }
            _rows.value = importedRows
        }
    }

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

        stopAlarm();
    }

    fun addRow(host: String = "", port: String = "") {
        _rows.value = _rows.value + (host to port)
    }

    fun updateRows(newRows: List<Pair<String, String>>) {
        _rows.value = newRows
        _portStatuses.value = List(newRows.size) { "Waiting" }
    }

    fun updateRow(index: Int, host: String, port: String) {
        _rows.value = _rows.value.toMutableList().apply {
            this[index] = host to port
        }
    }

    fun removeRow(index: Int) {
        _rows.value = _rows.value.toMutableList().apply {
            removeAt(index)
        }
    }

    private fun playAlarm(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.high_alert_warning_cut) // tu sonido en res/raw
            mediaPlayer?.isLooping = false // si querés que se repita en loop
        }

        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAlarm();
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

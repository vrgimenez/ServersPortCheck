package ar.vrx_design.serversportcheck.viewmodel

import android.app.Application
import android.media.MediaPlayer
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ar.vrx_design.serversportcheck.utils.RowStatusData
import ar.vrx_design.serversportcheck.utils.RowStatusSerializer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

// Extensión DataStore en Application
val Application.rowDataStore: DataStore<List<RowStatusData>> by dataStore(
    fileName = "rows.json",
    serializer = RowStatusSerializer
)

data class RowStatus(
    val host: String,
    val port: String,
    val status: String = "Esperando"
)

class PortCheckerViewModel(application: Application) : AndroidViewModel(application) {
    private val _rows = MutableStateFlow<List<RowStatus>>(emptyList())
    val rows: StateFlow<List<RowStatus>> = _rows.asStateFlow()

    private var monitoringJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    private val dataStore = application.rowDataStore

    init {
        // Cargar datos desde DataStore
        viewModelScope.launch {
            dataStore.data.collect { saved ->
                _rows.value = saved.map { RowStatus(it.host, it.port) }
            }
        }
    }

    private suspend fun persist() {
        dataStore.updateData { _rows.value.map { RowStatusData(it.host, it.port) } }
    }

    fun addRow(host: String, port: String) {
        _rows.update { it + RowStatus(host, port) }
        viewModelScope.launch { persist() }
    }

    fun removeRow(index: Int) {
        _rows.update { it.toMutableList().also { list -> list.removeAt(index) } }
        viewModelScope.launch { persist() }
    }

    fun updateRow(index: Int, host: String, port: String) {
        _rows.update { list ->
            list.toMutableList().also {
                it[index] = it[index].copy(host = host, port = port)
            }
        }
        viewModelScope.launch { persist() }
    }

    private fun updateRowStatus(index: Int, status: String) {
        _rows.update { list ->
            list.toMutableList().also {
                if (index in it.indices) {
                    it[index] = it[index].copy(status = status)
                }
            }
        }
    }

    fun startMonitoring() {
        if (monitoringJob?.isActive == true) return
        monitoringJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                _rows.value.forEachIndexed { index, row ->
                    val isOpen = checkPort(row.host, row.port.toIntOrNull() ?: return@forEachIndexed)
                    if (isOpen) {
                        updateRowStatus(index, "Abierto")
                    } else {
                        updateRowStatus(index, "Cerrado")
                        playAlarm()
                    }
                }
                delay(10_000)
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        stopAlarm()
        _rows.update { list -> list.map { it.copy(status = "Esperando") } }
    }

    private fun checkPort(host: String, port: Int, timeout: Int = 2000): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout)
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun playAlarm() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplication(), android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
        }
        mediaPlayer?.start()
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopMonitoring()
    }
}

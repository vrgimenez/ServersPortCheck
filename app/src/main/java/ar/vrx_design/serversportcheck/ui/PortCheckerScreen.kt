package ar.vrx_design.serversportcheck.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.vrx_design.serversportcheck.utils.PortCheckerDataStore
import ar.vrx_design.serversportcheck.viewmodel.PortCheckerViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

@Composable
fun PortCheckerScreenDynamic(context: Context) {
    val viewModel: PortCheckerViewModel = viewModel()
    val rows by viewModel.rows.collectAsState()
    val isChecking by viewModel.isChecking.collectAsState()
    val statuses by viewModel.portStatuses.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val savedRows = PortCheckerDataStore.loadRows(context)
        viewModel.updateRows(savedRows)
    }

    DisposableEffect(Unit) {
        onDispose {
            runBlocking { PortCheckerDataStore.saveRows(context, rows) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezados de columnas
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "#", modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Text(text = "Host", modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Text(text = "Port", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Text(text = "Stat", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Text(text = "Del", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Contenedor desplazable con LazyColumn
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Toma todo el espacio vertical disponible
                .fillMaxWidth()
        ) {
            items(rows.size) { index ->
                val (host, port) = rows[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically // Centrar contenido verticalmente
                ) {
                    Text(text = "${index + 1}", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
                    CompactTextField(
                        value = host,
                        onValueChange = { newHost ->
                            viewModel.updateRow(index, newHost, port)
                        },
                        modifier = Modifier.weight(2f)
                    )
                    CompactTextField(
                        value = port,
                        onValueChange = { newPort ->
                            viewModel.updateRow(index, host, newPort)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    StatusIndicator(
                        status = statuses.getOrNull(index),
                        modifier = Modifier.weight(0.6f)
                    )
                    IconButton(
                        onClick = { viewModel.removeRow(index) },
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar fila")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones para importar, exportar y agregar fila
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Button(onClick = {
                scope.launch {
                    val json = viewModel.exportToJson(context)
                    saveToFile(context, "hosts_ports.json", json)
                }
            }) {
                Text("Exportar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                scope.launch {
                    val json = readFromFile(context, "hosts_ports.json")
                    if (json != null) {
                        viewModel.importFromJson(context, json)
                    }
                }
            }) {
                Text("Importar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.addRow() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar fila")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para iniciar/detener monitoreo
        Button(
            onClick = {
                if (isChecking) {
                    viewModel.stopChecking()
                } else {
                    viewModel.startChecking(
                        rows.map { Pair(it.first, it.second.toIntOrNull() ?: 0) },
                        context
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isChecking) "Detener monitoreo" else "Iniciar monitoreo")
        }
    }
}

fun saveToFile(context: Context, filename: String, content: String) {
    val file = File(context.getExternalFilesDir(null), filename)
    file.writeText(content)
}

fun readFromFile(context: Context, filename: String): String? {
    val file = File(context.getExternalFilesDir(null), filename)
    return if (file.exists()) file.readText() else null
}

// Campo de texto compacto
@Composable
fun CompactTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,//.height(40.dp)
        singleLine = true,
        /*colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Gray,
            unfocusedIndicatorColor = Color.LightGray,
            disabledIndicatorColor = Color.Transparent
        ),*/
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
    )
}

// Indicador de estado con semáforo y texto debajo
@Composable
fun StatusIndicator(status: String?, modifier: Modifier = Modifier) {
    val color = when (status) {
        "Abierto" -> Color.Green
        "Cerrado" -> Color.Red
        else -> Color.Gray
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp) // Tamaño fijo del semáforo
            .padding(4.dp) // Espaciado interno opcional
    ) {
        Box(
            modifier = Modifier
                .size(16.dp) // Tamaño del círculo
                .background(color, shape = CircleShape)
        )
    }
}
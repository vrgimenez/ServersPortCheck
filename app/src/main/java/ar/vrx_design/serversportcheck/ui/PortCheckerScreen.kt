package ar.vrx_design.serversportcheck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.vrx_design.serversportcheck.viewmodel.PortCheckerViewModel

@Composable
fun PortCheckerScreen() {
    val viewModel: PortCheckerViewModel = viewModel()

    var host1 by remember { mutableStateOf("179.41.4.222") }
    var port1 by remember { mutableStateOf("40050") }

    var host2 by remember { mutableStateOf("179.41.4.222") }
    var port2 by remember { mutableStateOf("40051") }

    var host3 by remember { mutableStateOf("avlmaker.com.ar") }
    var port3 by remember { mutableStateOf("80") }

    val statuses by viewModel.portStatuses
    var isChecking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Títulos de las columnas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "#",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.2f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Host",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Puerto",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.6f),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Estado",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.8f),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Primera fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "1", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
            CompactTextField(value = host1, onValueChange = { host1 = it }, modifier = Modifier.weight(1f))
            CompactTextField(value = port1, onValueChange = { port1 = it }, modifier = Modifier.weight(0.6f))
            StatusIndicator(status = statuses.getOrNull(0))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Segunda fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "2", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
            CompactTextField(value = host2, onValueChange = { host2 = it }, modifier = Modifier.weight(1f))
            CompactTextField(value = port2, onValueChange = { port2 = it }, modifier = Modifier.weight(0.6f))
            StatusIndicator(status = statuses.getOrNull(1))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tercera fila
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "3", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Center)
            CompactTextField(value = host3, onValueChange = { host3 = it }, modifier = Modifier.weight(1f))
            CompactTextField(value = port3, onValueChange = { port3 = it }, modifier = Modifier.weight(0.6f))
            StatusIndicator(status = statuses.getOrNull(1))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar/detener monitoreo
        Button(
            onClick = {
                if (isChecking) {
                    viewModel.stopChecking()
                } else {
                    viewModel.startChecking(
                        listOf(
                            Pair(host1, port1.toIntOrNull() ?: 0),
                            Pair(host2, port2.toIntOrNull() ?: 0),
                            Pair(host3, port3.toIntOrNull() ?: 0)
                        )
                    )
                }
                isChecking = !isChecking
            }
        ) {
            Text(if (isChecking) "Detener monitoreo" else "Iniciar monitoreo")
        }
    }
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
fun StatusIndicator(status: String?) {
    val color = when {
        status?.contains("abierto", true) == true -> Color(0xFF008000)
        status?.contains("cerrado", true) == true -> Color(0xFF800000)
        else -> Color.Gray
    }
    Column(
        //modifier = Modifier.weight(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = status ?: "Esperando...",
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
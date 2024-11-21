package ar.vrx_design.serversportcheck.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ar.vrx_design.serversportcheck.viewmodel.PortCheckerViewModel

@Composable
fun PortCheckerScreen() {
    val viewModel: PortCheckerViewModel = viewModel()

    // Ahora usamos portStatus como un estado observable
    val status by viewModel.portStatus

    var host by remember { mutableStateOf("179.41.4.222") }
    var port by remember { mutableStateOf("40051") }
    var isChecking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Host") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Puerto") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isChecking) {
                    viewModel.startChecking(host, port.toInt(), 1_000L) // Intervalo de 1 segundos
                    isChecking = true
                }
            },
            enabled = !isChecking
        ) {
            Text("Iniciar monitoreo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Estado: $status", style = MaterialTheme.typography.bodyLarge)
    }
}

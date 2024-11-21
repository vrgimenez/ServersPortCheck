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

    var host1 by remember { mutableStateOf("179.41.4.222") }
    var port1 by remember { mutableStateOf("40050") }

    var host2 by remember { mutableStateOf("179.41.4.222") }
    var port2 by remember { mutableStateOf("40051") }

    val status by viewModel.portStatus
    var isChecking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primera fila de Host y Puerto
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = host1,
                onValueChange = { host1 = it },
                label = { Text("Host 1") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = port1,
                onValueChange = { port1 = it },
                label = { Text("Puerto 1") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Segunda fila de Host y Puerto
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = host2,
                onValueChange = { host2 = it },
                label = { Text("Host 2") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = port2,
                onValueChange = { port2 = it },
                label = { Text("Puerto 2") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar monitoreo
        Button(
            onClick = {
                if (!isChecking) {
                    viewModel.startChecking(
                        listOf(
                            Pair(host1, port1.toIntOrNull() ?: 0),
                            Pair(host2, port2.toIntOrNull() ?: 0)
                        )
                    )
                    isChecking = true
                }
            },
            enabled = !isChecking
        ) {
            Text("Iniciar monitoreo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Estado
        Text(text = "Estado:\n$status", style = MaterialTheme.typography.bodyLarge)
    }
}

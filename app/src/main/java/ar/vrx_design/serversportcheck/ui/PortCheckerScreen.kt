package ar.vrx_design.serversportcheck.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import ar.vrx_design.serversportcheck.viewmodel.PortCheckerViewModel

@Composable
fun PortCheckerScreen(viewModel: PortCheckerViewModel = viewModel()) {
    val rows by viewModel.rows.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("N°", modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
            Text("Host", modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
            Text("Puerto", modifier = Modifier.weight(2f), textAlign = TextAlign.Center)
            Text("Estado", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Borrar", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(rows) { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}", modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)

                    OutlinedTextField(
                        value = row.host,
                        onValueChange = { viewModel.updateRow(index, it, row.port) },
                        modifier = Modifier.weight(3f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = row.port,
                        onValueChange = { viewModel.updateRow(index, row.host, it) },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                when (row.status) {
                                    "Abierto" -> Color.Green
                                    "Cerrado" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                    )

                    Button(
                        onClick = { viewModel.removeRow(index) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("X")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { viewModel.addRow("", "") }) { Text("Agregar fila") }
            Button(onClick = { viewModel.startMonitoring() }) { Text("Iniciar") }
            Button(onClick = { viewModel.stopMonitoring() }) { Text("Detener") }
        }
    }
}

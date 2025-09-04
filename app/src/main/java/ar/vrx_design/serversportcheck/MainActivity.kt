package ar.vrx_design.serversportcheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import ar.vrx_design.serversportcheck.ui.PortCheckerScreen
import ar.vrx_design.serversportcheck.ui.theme.PortCheckerTheme
import ar.vrx_design.serversportcheck.viewmodel.PortCheckerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: PortCheckerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PortCheckerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PortCheckerScreen(viewModel)
                }
            }
        }
    }
}

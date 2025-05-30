package ar.vrx_design.serversportcheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import ar.vrx_design.serversportcheck.ui.PortCheckerScreenDynamic

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    PortCheckerScreenDynamic(context = this)
                }
            }
        }
    }
}

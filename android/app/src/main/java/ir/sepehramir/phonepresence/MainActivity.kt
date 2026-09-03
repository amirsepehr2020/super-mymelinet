package ir.sepehramir.phonepresence

import android.app.Activity
import android.os.Bundle
import android.os.PowerManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { PresenceApp() } }
}

@Composable
fun PresenceApp() {
    var name by remember { mutableStateOf("Sepehr") }
    var api by remember { mutableStateOf("https://YOUR-WORKER.workers.dev/presence") }
    var enabled by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val power = context.getSystemService(PowerManager::class.java)
    val cm = context.getSystemService(ConnectivityManager::class.java)
    val screenOn = power?.isInteractive == true
    val online = cm?.activeNetwork?.let { cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } == true
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Phone Presence", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(name, { name = it }, label = { Text("Device owner") })
                OutlinedTextField(api, { api = it }, label = { Text("Presence API") })
                Text("Internet: ${if (online) "Connected" else "Offline"}")
                Text("Screen: ${if (screenOn) "On" else "Off"}")
                Button(onClick = { enabled = !enabled }) { Text(if (enabled) "Tracking enabled" else "Start tracking") }
                Text("When enabled, the companion service should send only presence state (online, screen state, and timestamps) to your private API.")
            }
        }
    }
}

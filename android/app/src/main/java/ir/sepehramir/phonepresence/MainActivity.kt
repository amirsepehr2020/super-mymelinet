package ir.sepehramir.phonepresence

import android.os.Bundle
import android.os.Build
import android.content.Intent
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PresenceApp() }
    }
}

@Composable
fun PresenceApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val power = context.getSystemService(PowerManager::class.java)
    val cm = context.getSystemService(ConnectivityManager::class.java)
    val screenOn = power?.isInteractive == true
    val online = cm?.activeNetwork?.let {
        cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } == true

    val manufacturer = Build.MANUFACTURER.lowercase()
    val owner = if (manufacturer.contains("samsung")) "Sepehr" else "Amir"
    var enabled by remember { mutableStateOf(false) }

    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Phone Presence", style = MaterialTheme.typography.headlineMedium)
                Text("Device: $owner")
                Text("Internet: ${if (online) "Connected" else "Offline"}")
                Text("Screen: ${if (screenOn) "On" else "Off"}")
                Button(onClick = {
                    val intent = Intent(context, PresenceService::class.java).apply {
                        action = if (enabled) PresenceService.ACTION_STOP else PresenceService.ACTION_START
                    }
                    if (enabled) context.stopService(intent)
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent)
                        else context.startService(intent)
                    }
                    enabled = !enabled
                }) {
                    Text(if (enabled) "Stop tracking" else "Start tracking")
                }
                Text("Device identity is automatic: Samsung = Sepehr, other devices = Amir.")
            }
        }
    }
}

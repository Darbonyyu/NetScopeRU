package ru.netscope

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.weight
import androidx.hilt.navigation.compose.hiltViewModel
import ru.netscope.feature.monitor.MonitorScreen
import ru.netscope.feature.monitor.MonitorViewModel
import ru.netscope.feature.settings.SettingsScreen
import ru.netscope.feature.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()
        setContent {
            MaterialTheme { Surface {
                var tab by remember { mutableIntStateOf(0) }
                androidx.compose.foundation.layout.Column {
                    androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                        if (tab == 0) { val vm: MonitorViewModel = hiltViewModel(); MonitorScreen(vm, sessionId = 1L) }
                        else { val vm: SettingsViewModel = hiltViewModel(); SettingsScreen(vm) }
                    }
                    NavigationBar {
                        NavigationBarItem(tab == 0, { tab = 0 }, icon = { Text("◉") }, label = { Text("Монитор") })
                        NavigationBarItem(tab == 1, { tab = 1 }, icon = { Text("⚙") }, label = { Text("Настройки") })
                    }
                }
            } }
        }
    }
    private fun requestRequiredPermissions() {
        val permissions = buildList { add(Manifest.permission.ACCESS_FINE_LOCATION); add(Manifest.permission.READ_PHONE_STATE); if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS) }
        permissionLauncher.launch(permissions.toTypedArray())
    }
}

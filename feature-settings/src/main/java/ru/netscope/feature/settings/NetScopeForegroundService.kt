package ru.netscope.feature.settings
import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*
import ru.netscope.core.data.CellDataRepository
@AndroidEntryPoint class NetScopeForegroundService : Service() {
 @Inject lateinit var repository: CellDataRepository; private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO); private var collectionJob: Job? = null; private var sessionId = 0L
 override fun onCreate() { super.onCreate(); if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL, "NetScope RU", NotificationManager.IMPORTANCE_LOW)) }
 override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { if (intent?.action == ACTION_STOP) { collectionJob?.cancel(); serviceScope.launch { if (sessionId != 0L) repository.finishSession(sessionId); stopSelf() }; return START_NOT_STICKY }; if (collectionJob?.isActive == true) return START_STICKY; val notification = NotificationCompat.Builder(this, CHANNEL).setSmallIcon(android.R.drawable.ic_menu_info_details).setContentTitle("NetScope RU").setContentText("Фоновый сбор данных о сети").setOngoing(true).build(); ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, if (Build.VERSION.SDK_INT >= 29) android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION else 0); collectionJob = serviceScope.launch { sessionId = intent?.getLongExtra(EXTRA_SESSION_ID, 0L)?.takeIf { it != 0L } ?: repository.createSession("Фоновый сбор"); repository.liveMeasurements(sessionId).catch { stopSelf() }.collect() }; return START_STICKY }
 override fun onDestroy() { serviceScope.cancel(); super.onDestroy() }; override fun onBind(intent: Intent?): IBinder? = null
 companion object { const val ACTION_START = "ru.netscope.action.START_BACKGROUND_COLLECTION"; const val ACTION_STOP = "ru.netscope.action.STOP_BACKGROUND_COLLECTION"; const val EXTRA_SESSION_ID = "session_id"; private const val CHANNEL = "netscope_collection"; private const val NOTIFICATION_ID = 42 }
}

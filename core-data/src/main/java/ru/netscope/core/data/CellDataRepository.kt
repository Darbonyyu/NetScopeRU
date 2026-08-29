package ru.netscope.core.data
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import ru.netscope.core.data.db.CellMeasurementEntity
import ru.netscope.core.data.db.MeasurementDao
import ru.netscope.core.data.db.SessionEntity
import ru.netscope.core.telephony.CellDataCollector
import ru.netscope.core.telephony.model.CellMeasurement
import ru.netscope.core.telephony.model.ComponentCarrier
class CellDataRepository @Inject constructor(private val collector: CellDataCollector, private val dao: MeasurementDao, @ApplicationContext private val context: Context) {
 private val preloadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
 init { preloadScope.launch { if (dao.getOperatorCount() == 0) dao.insertOperators(readOperatorsAsset()) } }
 fun liveMeasurements(sessionId: Long): Flow<CellMeasurement> = collector.measurements.transform { cells -> cells.forEach { measurement -> dao.insertMeasurement(measurement.toEntity(sessionId)); emit(measurement) } }
 fun activeCarriers(): Flow<List<ComponentCarrier>> = collector.componentCarriers
 fun currentMeasurements(): Flow<List<CellMeasurement>> = collector.measurements
 fun getMeasurementsBySession(sessionId: Long): Flow<List<CellMeasurement>> = dao.getMeasurementsBySession(sessionId).map { list -> list.map(CellMeasurementEntity::toModel) }
 fun getMeasurementsInBounds(latMin: Double, latMax: Double, lonMin: Double, lonMax: Double): Flow<List<CellMeasurement>> = dao.getMeasurementsInBounds(latMin, latMax, lonMin, lonMax).map { list -> list.map(CellMeasurementEntity::toModel) }
 suspend fun createSession(title: String, startTime: Long = System.currentTimeMillis()): Long = dao.insertSession(SessionEntity(startTime = startTime, title = title))
 suspend fun ensureSession(sessionId: Long, title: String = "Мониторинг"): Long = if (dao.getSession(sessionId) != null) sessionId else createSession(title)
 fun sessions(): Flow<List<SessionEntity>> = dao.getSessions()
 suspend fun deleteSession(sessionId: Long) = dao.deleteSession(sessionId)
 suspend fun finishSession(sessionId: Long, endTime: Long = System.currentTimeMillis()) = dao.finishSession(sessionId, endTime)
 fun exportSessionToCsv(sessionId: Long): Flow<File> = flow { val target = File(context.cacheDir, "netscope-session-$sessionId.csv"); target.bufferedWriter().use { writer -> writer.appendLine("id,sessionId,timestamp,lat,lon,mcc,mnc,lac,cid,pci,tac,band,rsrp,rsrq,sinr,networkType,operatorName,isDataComplete,nsaGroupId"); dao.getMeasurementsForExport(sessionId).forEach { writer.appendLine(it.toCsvRow()) } }; emit(target) }
 private fun CellMeasurement.toEntity(sessionId: Long) = CellMeasurementEntity(sessionId = sessionId, timestamp = timestamp, lat = lat, lon = lon, mcc = mcc, mnc = mnc, lac = lac, cid = cid, pci = pci, tac = tac, band = band, rsrp = rsrp, rsrq = rsrq, sinr = sinr, networkType = networkType, operatorName = operatorName, isDataComplete = isDataComplete, nsaGroupId = nsaGroupId)
 private fun CellMeasurementEntity.toModel() = CellMeasurement(timestamp, lat, lon, mcc, mnc, lac, cid, pci, tac, band, rsrp, rsrq, sinr, networkType, operatorName, isDataComplete, nsaGroupId)
 private fun CellMeasurementEntity.toCsvRow(): String = listOf(id, sessionId, timestamp, lat, lon, mcc, mnc, lac, cid, pci, tac, band, rsrp, rsrq, sinr, networkType, operatorName, isDataComplete, nsaGroupId).joinToString(",") { csvEscape(it?.toString().orEmpty()) }
 private fun csvEscape(value: String): String = if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"${value.replace("\"", "\"\"")}\"" else value
 private fun readOperatorsAsset() = context.assets.open("operators_ru.json").bufferedReader().use { source -> Regex("\\{\\\"mnc\\\":(\\d+),\\\"name\\\":\\\"([^\\\"]+)\\\",\\\"isVirtual\\\":(true|false)\\}").findAll(source.readText()).map { match -> ru.netscope.core.data.db.OperatorEntity(match.groupValues[1].toInt(), match.groupValues[2], match.groupValues[3].toBoolean()) }.toList() }
}

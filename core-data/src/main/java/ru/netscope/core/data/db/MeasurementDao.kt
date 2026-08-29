package ru.netscope.core.data.db
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface MeasurementDao {
 @Insert suspend fun insertMeasurement(measurement: CellMeasurementEntity): Long
 @Insert suspend fun insertSession(session: SessionEntity): Long
 @Query("SELECT * FROM sessions ORDER BY startTime DESC") fun getSessions(): Flow<List<SessionEntity>>
 @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1") suspend fun getSession(sessionId: Long): SessionEntity?
 @Query("DELETE FROM sessions WHERE id = :sessionId") suspend fun deleteSession(sessionId: Long)
 @Query("UPDATE sessions SET endTime = :endTime WHERE id = :sessionId") suspend fun finishSession(sessionId: Long, endTime: Long)
 @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE) suspend fun insertOperators(operators: List<OperatorEntity>)
 @Query("SELECT COUNT(*) FROM operators") suspend fun getOperatorCount(): Int
 @Query("SELECT * FROM cell_measurements WHERE sessionId = :sessionId ORDER BY timestamp") fun getMeasurementsBySession(sessionId: Long): Flow<List<CellMeasurementEntity>>
 @Query("SELECT * FROM cell_measurements WHERE lat >= :latMin AND lat <= :latMax AND lon >= :lonMin AND lon <= :lonMax ORDER BY timestamp") fun getMeasurementsInBounds(latMin: Double, latMax: Double, lonMin: Double, lonMax: Double): Flow<List<CellMeasurementEntity>>
 @Query("SELECT * FROM cell_measurements WHERE sessionId = :sessionId ORDER BY timestamp") suspend fun getMeasurementsForExport(sessionId: Long): List<CellMeasurementEntity>
}

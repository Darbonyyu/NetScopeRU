package ru.netscope.core.data.db
import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [CellMeasurementEntity::class, OperatorEntity::class, SessionEntity::class], version = 1, exportSchema = true)
abstract class NetScopeDatabase : RoomDatabase() { abstract fun measurementDao(): MeasurementDao }

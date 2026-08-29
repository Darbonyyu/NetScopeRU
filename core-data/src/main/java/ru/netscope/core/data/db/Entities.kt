package ru.netscope.core.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "sessions")
data class SessionEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val startTime: Long, val endTime: Long? = null, val title: String)
@Entity(tableName = "operators", indices = [Index(value = ["mnc"], unique = true)])
data class OperatorEntity(@PrimaryKey val mnc: Int, val name: String, val isVirtual: Boolean)
@Entity(tableName = "cell_measurements", foreignKeys = [ForeignKey(entity = SessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)], indices = [Index("sessionId"), Index("lat", "lon")])
data class CellMeasurementEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val sessionId: Long, val timestamp: Long, val lat: Double?, val lon: Double?, val mcc: Int?, val mnc: Int?, val lac: Int?, val cid: Long?, val pci: Int?, val tac: Int?, val band: Int?, val rsrp: Int?, val rsrq: Int?, val sinr: Int?, val networkType: Int, val operatorName: String?, val isDataComplete: Boolean, val nsaGroupId: String?)

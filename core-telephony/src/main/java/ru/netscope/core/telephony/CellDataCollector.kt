package ru.netscope.core.telephony

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.telephony.CellIdentityGsm
import android.telephony.CellIdentityLte
import android.telephony.CellIdentityNr
import android.telephony.CellIdentityWcdma
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.CellSignalStrengthLte
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import ru.netscope.core.telephony.model.CellMeasurement
import ru.netscope.core.telephony.model.ComponentCarrier
import ru.netscope.core.telephony.model.LocationPoint
import ru.netscope.core.telephony.model.RussianOperators

class CellDataCollector(
    context: Context,
    private val locationProvider: () -> LocationPoint? = { null },
) {
    private val telephony = context.getSystemService(TelephonyManager::class.java)
    private val handler = Handler(Looper.getMainLooper())

    val measurements: Flow<List<CellMeasurement>> = callbackFlow {
        if (!hasPermissions(context)) {
            close(SecurityException("ACCESS_FINE_LOCATION and READ_PHONE_STATE are required"))
            return@callbackFlow
        }

        val closed = AtomicBoolean(false)
        val poll = object : Runnable {
            override fun run() {
                if (closed.get()) return
                @Suppress("DEPRECATION")
                val cells = telephony.allCellInfo.orEmpty()
                trySend(parse(cells))
                handler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
        handler.post(poll)
        awaitClose {
            closed.set(true)
            handler.removeCallbacks(poll)
        }
    }.onStart { emit(emptyList()) }.distinctUntilChanged()

    // Physical-channel callbacks vary between Android vendor implementations.
    // Keep this optional stream empty until a compatible callback is available.
    val componentCarriers: Flow<List<ComponentCarrier>> = callbackFlow {
        awaitClose {}
    }.distinctUntilChanged()

    private fun parse(cells: List<CellInfo>): List<CellMeasurement> {
        val timestamp = System.currentTimeMillis()
        val point = locationProvider()
        val parsed = cells.mapNotNull { parseCell(it, timestamp, point) }
        val hasLte = parsed.any { it.networkType == TelephonyManager.NETWORK_TYPE_LTE }
        val hasNr = parsed.any { it.networkType == TelephonyManager.NETWORK_TYPE_NR }
        if (!hasLte || !hasNr) return parsed
        val group = "nsa-$timestamp"
        return parsed.map {
            if (it.networkType == TelephonyManager.NETWORK_TYPE_LTE || it.networkType == TelephonyManager.NETWORK_TYPE_NR) {
                it.copy(nsaGroupId = group)
            } else it
        }
    }

    private fun parseCell(info: CellInfo, timestamp: Long, point: LocationPoint?): CellMeasurement? {
        val signal = info.cellSignalStrength
        val identity = info.cellIdentity
        val networkType: Int
        var mcc: Int? = null
        var mnc: Int? = null
        var lac: Int? = null
        var cid: Long? = null
        var pci: Int? = null
        var tac: Int? = null
        when (info) {
            is CellInfoGsm -> {
                networkType = TelephonyManager.NETWORK_TYPE_GSM
                val id = identity as CellIdentityGsm
                mcc = id.mcc.takeUnless { it == CellInfo.UNAVAILABLE }
                mnc = id.mnc.takeUnless { it == CellInfo.UNAVAILABLE }
                lac = id.lac.takeUnless { it == CellInfo.UNAVAILABLE }
                cid = id.cid.takeUnless { it == CellInfo.UNAVAILABLE }?.toLong()
            }
            is CellInfoWcdma -> {
                networkType = TelephonyManager.NETWORK_TYPE_UMTS
                val id = identity as CellIdentityWcdma
                mcc = id.mcc.takeUnless { it == CellInfo.UNAVAILABLE }
                mnc = id.mnc.takeUnless { it == CellInfo.UNAVAILABLE }
                lac = id.lac.takeUnless { it == CellInfo.UNAVAILABLE }
                cid = id.cid.takeUnless { it == CellInfo.UNAVAILABLE }?.toLong()
                pci = id.psc.takeUnless { it == CellInfo.UNAVAILABLE }
            }
            is CellInfoLte -> {
                networkType = TelephonyManager.NETWORK_TYPE_LTE
                val id = identity as CellIdentityLte
                mcc = id.mcc.takeUnless { it == CellInfo.UNAVAILABLE }
                mnc = id.mnc.takeUnless { it == CellInfo.UNAVAILABLE }
                cid = id.ci.takeUnless { it == CellInfo.UNAVAILABLE }?.toLong()
                pci = id.pci.takeUnless { it == CellInfo.UNAVAILABLE }
                tac = id.tac.takeUnless { it == CellInfo.UNAVAILABLE }
            }
            is CellInfoNr -> {
                networkType = TelephonyManager.NETWORK_TYPE_NR
                val id = identity as CellIdentityNr
                mcc = id.mccString?.toIntOrNull()
                mnc = id.mncString?.toIntOrNull()
                cid = id.nci.takeUnless { it == CellInfo.UNAVAILABLE.toLong() }
                pci = id.pci.takeUnless { it == CellInfo.UNAVAILABLE }
                tac = id.tac.takeUnless { it == CellInfo.UNAVAILABLE }
            }
            else -> return null
        }
        val unavailable = CellInfo.UNAVAILABLE
        val rsrp = signal.dbm.takeUnless { it == unavailable }
        val lteSignal = signal as? CellSignalStrengthLte
        val operator = RussianOperators.find(mcc, mnc)?.name
        val complete = mcc != null && mnc != null && cid != null && rsrp != null
        return CellMeasurement(
            timestamp, point?.latitude, point?.longitude, mcc, mnc, lac, cid, pci, tac,
            null, rsrp, lteSignal?.rsrq, lteSignal?.rssnr, networkType, operator, complete,
        )
    }

    private fun hasPermissions(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val UPDATE_INTERVAL_MS = 2_000L
    }
}

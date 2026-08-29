package ru.netscope.core.telephony.model

data class CellMeasurement(val timestamp: Long, val lat: Double?, val lon: Double?, val mcc: Int?, val mnc: Int?, val lac: Int?, val cid: Long?, val pci: Int?, val tac: Int?, val band: Int?, val rsrp: Int?, val rsrq: Int?, val sinr: Int?, val networkType: Int, val operatorName: String?, val isDataComplete: Boolean, val nsaGroupId: String? = null)
data class ComponentCarrier(val networkType: Int, val downlinkChannelNumber: Int?, val downlinkBandwidthKhz: Int?, val pci: Int?)
data class LocationPoint(val latitude: Double, val longitude: Double)

package ru.netscope.feature.settings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.netscope.core.data.*
import ru.netscope.core.data.db.SessionEntity
import ru.netscope.core.telephony.model.CellMeasurement
import java.io.File

data class DeviceDiagnostics(val manufacturer: String = Build.MANUFACTURER, val model: String = Build.MODEL, val hardware: String = Build.HARDWARE, val availableFields: List<String> = emptyList(), val hasCompleteData: Boolean? = null)
@HiltViewModel class SettingsViewModel @Inject constructor(private val settings: SettingsRepository, private val repository: CellDataRepository) : ViewModel() {
 val beginnerMode = settings.beginnerMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true); val sessions = repository.sessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()); val backgroundCollection = settings.backgroundCollection.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false); private val _diagnostics = MutableStateFlow(DeviceDiagnostics()); val diagnostics = _diagnostics.asStateFlow(); private val _exportedFile = MutableStateFlow<File?>(null); val exportedFile = _exportedFile.asStateFlow()
 init { viewModelScope.launch { repository.currentMeasurements().catch { }.collect { updateDiagnostics(it.firstOrNull()) } } }
 fun setBeginnerMode(enabled: Boolean) = viewModelScope.launch { settings.setBeginnerMode(enabled) }; fun setBackgroundCollection(enabled: Boolean) = viewModelScope.launch { settings.setBackgroundCollection(enabled) }; fun deleteSession(id: Long) = viewModelScope.launch { repository.deleteSession(id) }; fun exportSession(id: Long) = viewModelScope.launch { repository.exportSessionToCsv(id).collect { _exportedFile.value = it } }
 fun updateDiagnostics(measurement: CellMeasurement?) { _diagnostics.value = DeviceDiagnostics(availableFields = measurement?.availableFields().orEmpty(), hasCompleteData = measurement?.isDataComplete) }
 private fun CellMeasurement.availableFields() = buildList { if (lat != null && lon != null) add("Координаты"); if (mcc != null && mnc != null) add("MCC/MNC"); if (lac != null) add("LAC"); if (cid != null) add("CID"); if (pci != null) add("PCI"); if (tac != null) add("TAC"); if (band != null) add("Band"); if (rsrp != null) add("RSRP"); if (rsrq != null) add("RSRQ"); if (sinr != null) add("SINR"); if (operatorName != null) add("Оператор") }
}

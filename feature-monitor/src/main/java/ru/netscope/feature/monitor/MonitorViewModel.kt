package ru.netscope.feature.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.netscope.core.data.CellDataRepository
import ru.netscope.core.data.SettingsRepository
import ru.netscope.core.telephony.model.CellMeasurement
import ru.netscope.core.telephony.model.ComponentCarrier

data class MonitorUiState(val current: CellMeasurement? = null, val history: List<CellMeasurement> = emptyList(), val carriers: List<ComponentCarrier> = emptyList())
@HiltViewModel
class MonitorViewModel @Inject constructor(private val repository: CellDataRepository, private val settingsRepository: SettingsRepository) : ViewModel() {
 private val current = MutableStateFlow<CellMeasurement?>(null); private val history = MutableStateFlow<List<CellMeasurement>>(emptyList()); private val carriers = MutableStateFlow<List<ComponentCarrier>>(emptyList()); private var collectionJob: Job? = null; private var carriersJob: Job? = null
 val beginnerMode: StateFlow<Boolean> = settingsRepository.beginnerMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
 val uiState: StateFlow<MonitorUiState> = combine(current, history, carriers, ::MonitorUiState).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonitorUiState())
 fun start(sessionId: Long) { collectionJob?.cancel(); carriersJob?.cancel(); viewModelScope.launch { val actualSessionId = repository.ensureSession(sessionId); collectionJob = launch { repository.liveMeasurements(actualSessionId).collect { measurement -> current.value = measurement; history.value = (history.value + measurement).filter { it.timestamp >= System.currentTimeMillis() - 5 * 60_000L }.takeLast(300) } }; carriersJob = launch { repository.activeCarriers().collect { carriers.value = it } } } }
 fun setBeginnerMode(enabled: Boolean) { viewModelScope.launch { settingsRepository.setBeginnerMode(enabled) } }
}

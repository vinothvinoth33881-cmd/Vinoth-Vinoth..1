package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.KidneyDatabase
import com.example.data.KidneyDiagnosisEntity
import com.example.data.KidneyRepository
import com.example.ml.KidneyInferenceEngine
import com.example.model.BenchmarkCase
import com.example.model.BenchmarkDataset
import com.example.model.DiagnosisClass
import com.example.model.ModelArchitecture
import com.example.model.PredictionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppDestination(val title: String, val route: String) {
    DIAGNOSIS("Diagnosis", "diagnosis"),
    CASES("Case History", "cases"),
    BENCHMARKS("Model Metrics", "benchmarks"),
    REPORT("Clinical Report", "report")
}

class KidneyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KidneyRepository

    init {
        val db = KidneyDatabase.getInstance(application)
        repository = KidneyRepository(db.kidneyDiagnosisDao())
        viewModelScope.launch(Dispatchers.IO) {
            repository.populateInitialDataIfEmpty()
        }
    }

    // Navigation State
    private val _currentDestination = MutableStateFlow(AppDestination.DIAGNOSIS)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    // Active Diagnosis Input State
    val benchmarkCases = BenchmarkDataset.sampleCases

    private val _selectedBenchmark = MutableStateFlow<BenchmarkCase?>(benchmarkCases.first())
    val selectedBenchmark: StateFlow<BenchmarkCase?> = _selectedBenchmark.asStateFlow()

    private val _userImageUri = MutableStateFlow<Uri?>(null)
    val userImageUri: StateFlow<Uri?> = _userImageUri.asStateFlow()

    private val _selectedModel = MutableStateFlow(ModelArchitecture.EFFICIENTNET_B4)
    val selectedModel: StateFlow<ModelArchitecture> = _selectedModel.asStateFlow()

    // Patient clinical parameters
    val patientId = MutableStateFlow("PT-94021")
    val patientAge = MutableStateFlow(58)
    val patientGender = MutableStateFlow("Male")
    val scanType = MutableStateFlow("Axial Contrast CT")
    val egfr = MutableStateFlow(64.2)
    val serumCreatinine = MutableStateFlow(1.38)
    val bun = MutableStateFlow(24.5)
    val urineProtein = MutableStateFlow("1+")
    val bloodPressure = MutableStateFlow("142/88")
    val symptoms = MutableStateFlow("Painless gross hematuria, left flank fullness")

    // Inference State
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _predictionResult = MutableStateFlow<PredictionResult?>(null)
    val predictionResult: StateFlow<PredictionResult?> = _predictionResult.asStateFlow()

    private val _savedCaseSuccessMessage = MutableStateFlow<String?>(null)
    val savedCaseSuccessMessage: StateFlow<String?> = _savedCaseSuccessMessage.asStateFlow()

    // History Cases Flow & Filtering
    val allCases: StateFlow<List<KidneyDiagnosisEntity>> = repository.allDiagnoses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchQuery = MutableStateFlow("")
    val filterDiagnosisClass = MutableStateFlow<String?>("ALL")

    val filteredCases: StateFlow<List<KidneyDiagnosisEntity>> = combine(
        allCases,
        searchQuery,
        filterDiagnosisClass
    ) { cases, query, filterClass ->
        cases.filter { caseItem ->
            val matchesQuery = query.isBlank() ||
                    caseItem.patientId.contains(query, ignoreCase = true) ||
                    caseItem.primaryDiagnosis.contains(query, ignoreCase = true) ||
                    caseItem.symptoms.contains(query, ignoreCase = true)

            val matchesFilter = filterClass == null || filterClass == "ALL" ||
                    caseItem.primaryDiagnosis.equals(filterClass, ignoreCase = true)

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active case selected for detailed view or report
    private val _activeCaseDetail = MutableStateFlow<KidneyDiagnosisEntity?>(null)
    val activeCaseDetail: StateFlow<KidneyDiagnosisEntity?> = _activeCaseDetail.asStateFlow()

    init {
        // Run initial inference on default loaded benchmark case
        runDiagnosis()
    }

    fun selectBenchmarkCase(case: BenchmarkCase) {
        _selectedBenchmark.value = case
        _userImageUri.value = null
        patientId.value = case.caseId
        patientAge.value = case.patientAge
        patientGender.value = case.patientGender
        scanType.value = case.scanType
        egfr.value = case.egfr
        serumCreatinine.value = case.serumCreatinine
        bun.value = case.bun
        urineProtein.value = case.urineProtein
        bloodPressure.value = "${case.systolicBp}/${case.diastolicBp}"
        symptoms.value = case.symptoms
        runDiagnosis()
    }

    fun selectUserImage(uri: Uri) {
        _userImageUri.value = uri
        _selectedBenchmark.value = null
        patientId.value = "PT-${(10000..99999).random()}"
        runDiagnosis()
    }

    fun setModel(model: ModelArchitecture) {
        _selectedModel.value = model
        runDiagnosis()
    }

    fun runDiagnosis() {
        if (_isAnalyzing.value) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _savedCaseSuccessMessage.value = null

            val userUri = _userImageUri.value
            val bitmap = if (userUri != null) {
                withContext(Dispatchers.IO) {
                    try {
                        val input = getApplication<Application>().contentResolver.openInputStream(userUri)
                        BitmapFactory.decodeStream(input)
                    } catch (e: Exception) {
                        null
                    }
                }
            } else {
                null
            }

            val result = KidneyInferenceEngine.analyzeScan(
                bitmap = bitmap,
                selectedBenchmark = _selectedBenchmark.value,
                model = _selectedModel.value,
                egfr = egfr.value,
                creatinine = serumCreatinine.value,
                bun = bun.value,
                urineProtein = urineProtein.value,
                symptoms = symptoms.value
            )

            _predictionResult.value = result
            _isAnalyzing.value = false
        }
    }

    fun saveCurrentCase() {
        val result = _predictionResult.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val entity = KidneyDiagnosisEntity(
                patientId = patientId.value,
                patientAge = patientAge.value,
                patientGender = patientGender.value,
                scanType = scanType.value,
                imagePathOrUri = _userImageUri.value?.toString() ?: (_selectedBenchmark.value?.caseId ?: "benchmark_case"),
                primaryDiagnosis = result.primaryClass.name,
                confidence = result.confidence,
                probNormal = result.probabilities[DiagnosisClass.NORMAL] ?: 0f,
                probCyst = result.probabilities[DiagnosisClass.CYST] ?: 0f,
                probStone = result.probabilities[DiagnosisClass.STONE] ?: 0f,
                probTumor = result.probabilities[DiagnosisClass.TUMOR] ?: 0f,
                activeModel = result.modelUsed.shortName,
                egfr = egfr.value,
                serumCreatinine = serumCreatinine.value,
                bun = bun.value,
                urineProtein = urineProtein.value,
                bloodPressure = bloodPressure.value,
                symptoms = symptoms.value,
                riskLevel = result.riskLevel,
                ckdStage = result.ckdStage,
                reviewStatus = "Pending Radiologist Review",
                clinicianNotes = "Case evaluated with ${result.modelUsed.displayName}. Confidence: ${(result.confidence * 100).let { "%.1f".format(it) }}%."
            )

            val newId = repository.insertDiagnosis(entity)
            _activeCaseDetail.value = entity.copy(id = newId)
            _savedCaseSuccessMessage.value = "Case ${patientId.value} saved successfully to clinical database."
        }
    }

    fun selectCaseForDetail(case: KidneyDiagnosisEntity) {
        _activeCaseDetail.value = case
        _currentDestination.value = AppDestination.REPORT
    }

    fun updateCaseReviewStatus(id: Long, newStatus: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReviewStatus(id, newStatus, notes)
            val updated = repository.getDiagnosisById(id)
            if (_activeCaseDetail.value?.id == id) {
                _activeCaseDetail.value = updated
            }
        }
    }

    fun deleteCase(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDiagnosis(id)
            if (_activeCaseDetail.value?.id == id) {
                _activeCaseDetail.value = null
            }
        }
    }

    fun clearNotification() {
        _savedCaseSuccessMessage.value = null
    }
}

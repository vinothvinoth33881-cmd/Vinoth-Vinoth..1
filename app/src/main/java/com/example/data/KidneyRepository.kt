package com.example.data

import com.example.model.DiagnosisClass
import kotlinx.coroutines.flow.Flow

class KidneyRepository(private val dao: KidneyDiagnosisDao) {
    val allDiagnoses: Flow<List<KidneyDiagnosisEntity>> = dao.getAllDiagnoses()

    suspend fun insertDiagnosis(diagnosis: KidneyDiagnosisEntity): Long {
        return dao.insertDiagnosis(diagnosis)
    }

    suspend fun updateDiagnosis(diagnosis: KidneyDiagnosisEntity) {
        dao.updateDiagnosis(diagnosis)
    }

    suspend fun updateReviewStatus(id: Long, newStatus: String, notes: String) {
        dao.updateReviewStatus(id, newStatus, notes)
    }

    suspend fun deleteDiagnosis(id: Long) {
        dao.deleteDiagnosisById(id)
    }

    suspend fun getDiagnosisById(id: Long): KidneyDiagnosisEntity? {
        return dao.getDiagnosisById(id)
    }

    suspend fun populateInitialDataIfEmpty() {
        if (dao.getCount() == 0) {
            val sampleCases = listOf(
                KidneyDiagnosisEntity(
                    patientId = "PT-94021",
                    patientAge = 58,
                    patientGender = "Male",
                    scanType = "Axial Contrast CT (Nephrographic)",
                    imagePathOrUri = "sample_tumor",
                    primaryDiagnosis = DiagnosisClass.TUMOR.name,
                    confidence = 0.968f,
                    probNormal = 0.012f,
                    probCyst = 0.015f,
                    probStone = 0.005f,
                    probTumor = 0.968f,
                    activeModel = "EfficientNet-B4",
                    egfr = 64.2,
                    serumCreatinine = 1.38,
                    bun = 24.5,
                    urineProtein = "1+",
                    bloodPressure = "142/88",
                    symptoms = "Painless gross hematuria, left flank fullness",
                    riskLevel = "High / Immediate Surgical Review",
                    ckdStage = "CKD Stage 2 (Mild GFR reduction)",
                    reviewStatus = "Pending Radiologist Review",
                    clinicianNotes = "3.8cm enhancing mass at the upper pole of left kidney, suspect stage T1a RCC.",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                KidneyDiagnosisEntity(
                    patientId = "PT-81204",
                    patientAge = 42,
                    patientGender = "Female",
                    scanType = "Non-Contrast Renal Helical CT",
                    imagePathOrUri = "sample_stone",
                    primaryDiagnosis = DiagnosisClass.STONE.name,
                    confidence = 0.982f,
                    probNormal = 0.008f,
                    probCyst = 0.005f,
                    probStone = 0.982f,
                    probTumor = 0.005f,
                    activeModel = "ResNet-50",
                    egfr = 88.0,
                    serumCreatinine = 1.05,
                    bun = 18.0,
                    urineProtein = "Trace",
                    bloodPressure = "130/84",
                    symptoms = "Acute severe right colicky flank pain radiating to groin, nausea",
                    riskLevel = "Moderate / Acute Pain Triage",
                    ckdStage = "CKD Stage 1 (Preserved function)",
                    reviewStatus = "Verified by Physician",
                    clinicianNotes = "6.2mm calculus in right proximal ureteropelvic junction with mild upstream pelviectasis.",
                    timestamp = System.currentTimeMillis() - 86400000L * 4
                ),
                KidneyDiagnosisEntity(
                    patientId = "PT-77319",
                    patientAge = 65,
                    patientGender = "Male",
                    scanType = "Abdominal CT with IV Contrast",
                    imagePathOrUri = "sample_cyst",
                    primaryDiagnosis = DiagnosisClass.CYST.name,
                    confidence = 0.974f,
                    probNormal = 0.015f,
                    probCyst = 0.974f,
                    probStone = 0.006f,
                    probTumor = 0.005f,
                    activeModel = "Ensemble",
                    egfr = 72.5,
                    serumCreatinine = 1.20,
                    bun = 21.0,
                    urineProtein = "Negative",
                    bloodPressure = "128/80",
                    symptoms = "Incidental finding during staging for diverticulitis, asymptomatic",
                    riskLevel = "Low / Routine Surveillance",
                    ckdStage = "CKD Stage 2 (Age-expected mild reduction)",
                    reviewStatus = "Verified by Physician",
                    clinicianNotes = "Simple cortical renal cyst right mid-pole, 2.4cm, Bosniak Category I benign.",
                    timestamp = System.currentTimeMillis() - 86400000L * 7
                ),
                KidneyDiagnosisEntity(
                    patientId = "PT-60293",
                    patientAge = 34,
                    patientGender = "Female",
                    scanType = "Axial Contrast CT",
                    imagePathOrUri = "sample_normal",
                    primaryDiagnosis = DiagnosisClass.NORMAL.name,
                    confidence = 0.991f,
                    probNormal = 0.991f,
                    probCyst = 0.004f,
                    probStone = 0.003f,
                    probTumor = 0.002f,
                    activeModel = "EfficientNet-B4",
                    egfr = 104.0,
                    serumCreatinine = 0.78,
                    bun = 14.0,
                    urineProtein = "Negative",
                    bloodPressure = "118/76",
                    symptoms = "Workup for intermittent vague lumbar ache, non-nephric origin",
                    riskLevel = "Low / Within Normal Limits",
                    ckdStage = "Normal Renal Function",
                    reviewStatus = "Verified by Physician",
                    clinicianNotes = "Bilateral kidneys symmetric with sharp cortical margins and normal excretion.",
                    timestamp = System.currentTimeMillis() - 86400000L * 10
                )
            )
            for (caseEntity in sampleCases) {
                dao.insertDiagnosis(caseEntity)
            }
        }
    }
}

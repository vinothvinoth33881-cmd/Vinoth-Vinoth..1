package com.example.model

import com.example.R

object BenchmarkDataset {
    val sampleCases: List<BenchmarkCase> = listOf(
        BenchmarkCase(
            caseId = "CASE-TUM-01",
            title = "Left Upper Pole Renal Mass (Suspected RCC)",
            patientAge = 58,
            patientGender = "Male",
            scanType = "Axial Contrast CT (Nephrographic Phase)",
            groundTruth = DiagnosisClass.TUMOR,
            clinicalPresentation = "58-year-old male presenting with intermittent painless gross hematuria and dull left flank discomfort for 3 weeks.",
            egfr = 64.2,
            serumCreatinine = 1.38,
            bun = 24.5,
            urineProtein = "1+",
            systolicBp = 142,
            diastolicBp = 88,
            symptoms = "Painless gross hematuria, left flank fullness, mild fatigue",
            defaultProbabilities = mapOf(
                DiagnosisClass.TUMOR to 0.968f,
                DiagnosisClass.CYST to 0.015f,
                DiagnosisClass.NORMAL to 0.012f,
                DiagnosisClass.STONE to 0.005f
            ),
            sampleImageRes = R.drawable.sample_kidney_ct,
            heatmapCenter = Pair(0.68f, 0.44f)
        ),
        BenchmarkCase(
            caseId = "CASE-STN-02",
            title = "Right Pelvicalyceal Calculus (Nephrolithiasis)",
            patientAge = 42,
            patientGender = "Female",
            scanType = "Non-Contrast Helical Abdominal CT",
            groundTruth = DiagnosisClass.STONE,
            clinicalPresentation = "42-year-old female with sudden acute onset severe right colicky flank pain radiating toward the groin accompanied by nausea and microhematuria.",
            egfr = 88.0,
            serumCreatinine = 1.05,
            bun = 18.0,
            urineProtein = "Trace",
            systolicBp = 130,
            diastolicBp = 84,
            symptoms = "Severe right flank colicky pain, nausea, microscopic hematuria",
            defaultProbabilities = mapOf(
                DiagnosisClass.STONE to 0.982f,
                DiagnosisClass.NORMAL to 0.008f,
                DiagnosisClass.CYST to 0.005f,
                DiagnosisClass.TUMOR to 0.005f
            ),
            sampleImageRes = R.drawable.sample_kidney_ct,
            heatmapCenter = Pair(0.35f, 0.52f)
        ),
        BenchmarkCase(
            caseId = "CASE-CYS-03",
            title = "Simple Right Cortical Cyst (Bosniak Category I)",
            patientAge = 65,
            patientGender = "Male",
            scanType = "Abdominal CT with IV Contrast",
            groundTruth = DiagnosisClass.CYST,
            clinicalPresentation = "65-year-old male with incidental thin-walled fluid collection identified on routine health check-up scan. Clinically asymptomatic.",
            egfr = 72.5,
            serumCreatinine = 1.20,
            bun = 21.0,
            urineProtein = "Negative",
            systolicBp = 128,
            diastolicBp = 80,
            symptoms = "Asymptomatic (Incidental radiology finding)",
            defaultProbabilities = mapOf(
                DiagnosisClass.CYST to 0.974f,
                DiagnosisClass.NORMAL to 0.015f,
                DiagnosisClass.STONE to 0.006f,
                DiagnosisClass.TUMOR to 0.005f
            ),
            sampleImageRes = R.drawable.sample_kidney_ct,
            heatmapCenter = Pair(0.72f, 0.58f)
        ),
        BenchmarkCase(
            caseId = "CASE-NRM-04",
            title = "Unremarkable Bilateral Renal Parenchyma (Normal)",
            patientAge = 34,
            patientGender = "Female",
            scanType = "Axial Contrast CT",
            groundTruth = DiagnosisClass.NORMAL,
            clinicalPresentation = "34-year-old female evaluated following minor motor vehicle collision. No flank hematoma or parenchymal laceration.",
            egfr = 104.0,
            serumCreatinine = 0.78,
            bun = 14.0,
            urineProtein = "Negative",
            systolicBp = 118,
            diastolicBp = 76,
            symptoms = "Mild musculoskeletal lumbar tenderness only",
            defaultProbabilities = mapOf(
                DiagnosisClass.NORMAL to 0.991f,
                DiagnosisClass.CYST to 0.004f,
                DiagnosisClass.STONE to 0.003f,
                DiagnosisClass.TUMOR to 0.002f
            ),
            sampleImageRes = R.drawable.sample_kidney_ct,
            heatmapCenter = Pair(0.50f, 0.50f)
        )
    )
}

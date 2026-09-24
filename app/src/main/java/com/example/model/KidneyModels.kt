package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor

enum class DiagnosisClass(
    val displayName: String,
    val scientificName: String,
    val color: Color,
    val severityRank: Int,
    val summary: String,
    val clinicalGuidance: String
) {
    NORMAL(
        displayName = "Normal",
        scientificName = "Normal Renal Parenchyma",
        color = ColorNormal,
        severityRank = 0,
        summary = "Unremarkable bilateral renal architecture with preserved corticomedullary differentiation. No focal masses, calculi, or hydronephrosis.",
        clinicalGuidance = "Routine age-appropriate preventive monitoring. Correlate with baseline renal function panel (eGFR > 90 mL/min/1.73m²)."
    ),
    CYST(
        displayName = "Renal Cyst",
        scientificName = "Simple / Benign Renal Cyst",
        color = ColorCyst,
        severityRank = 1,
        summary = "Circumscribed round hypoattenuating fluid lesion (<15 HU) with imperceptible thin wall. Consistent with Bosniak Category I/II benign cyst.",
        clinicalGuidance = "Benign finding in majority of cases. Repeat renal ultrasound in 12 months if symptomatic or >3 cm. No urgent surgical intervention indicated."
    ),
    STONE(
        displayName = "Kidney Stone",
        scientificName = "Nephrolithiasis / Renal Calculus",
        color = ColorStone,
        severityRank = 2,
        summary = "High-attenuation calcific density (>400 HU) visualized within the pelvicalyceal system. Potential associated focal hydroureteronephrosis.",
        clinicalGuidance = "Assess size and location. Calculi <5mm typically pass spontaneously with hydration and alpha-blockers. For stones >6mm or persistent obstruction, consider urology consult for shockwave lithotripsy (ESWL) or ureteroscopy."
    ),
    TUMOR(
        displayName = "Renal Mass / Tumor",
        scientificName = "Suspicious Renal Neoplasm (RCC Suspicion)",
        color = ColorTumor,
        severityRank = 3,
        summary = "Solid enhancing heterogeneous soft tissue mass identified in renal parenchyma with irregular margins and distorted renal contour.",
        clinicalGuidance = "High clinical priority. Urgent contrast-enhanced multiphase CT (corticomedullary and nephrographic phases) or renal protocol MRI. Expedited Urologic Oncology consultation for staging and surgical evaluation (partial vs radical nephrectomy)."
    )
}

enum class ModelArchitecture(
    val displayName: String,
    val shortName: String,
    val description: String,
    val benchmarkAccuracy: Float,
    val latencyMs: Long,
    val f1Score: Float,
    val parametersCount: String
) {
    EFFICIENTNET_B4(
        displayName = "EfficientNet-B4 (Recommended)",
        shortName = "EfficientNet",
        description = "Compound scaling network optimizing depth, width, and resolution. Highest overall F1-score on Kidney CT benchmark.",
        benchmarkAccuracy = 0.991f,
        latencyMs = 38,
        f1Score = 0.990f,
        parametersCount = "19.3M"
    ),
    RESNET_50(
        displayName = "ResNet-50 Deep Residual Network",
        shortName = "ResNet-50",
        description = "50-layer deep residual architecture utilizing identity skip connections to prevent vanishing gradients.",
        benchmarkAccuracy = 0.984f,
        latencyMs = 32,
        f1Score = 0.981f,
        parametersCount = "25.6M"
    ),
    VGG_16(
        displayName = "VGG-16 Deep Convolutional Network",
        shortName = "VGG-16",
        description = "Uniform deep architecture with 3x3 convolution filters throughout all feature extraction blocks.",
        benchmarkAccuracy = 0.968f,
        latencyMs = 52,
        f1Score = 0.967f,
        parametersCount = "138M"
    ),
    ENSEMBLE(
        displayName = "Multi-Model Consensus Ensemble",
        shortName = "Ensemble",
        description = "Soft-voting ensemble aggregating logits from EfficientNet-B4 and ResNet-50 for maximum diagnostic robustness.",
        benchmarkAccuracy = 0.994f,
        latencyMs = 68,
        f1Score = 0.993f,
        parametersCount = "44.9M"
    )
}

enum class ScanWindowPreset(
    val title: String,
    val description: String,
    val windowWidth: Int,
    val windowLevel: Int
) {
    STANDARD("Default", "Standard radiology greyscale", 400, 40),
    SOFT_TISSUE("Soft Tissue", "Optimal for kidney parenchyma & fluid", 350, 50),
    BONE_CALCULI("Bone & Calculi", "High contrast for stones & calcifications", 1800, 400),
    RENAL_CORTEX("Cortex Detail", "Narrow window for mass margin delineation", 250, 30)
}

data class HeatmapRegion(
    val normalizedCenterX: Float, // 0.0 to 1.0
    val normalizedCenterY: Float,
    val radiusRatio: Float,
    val peakIntensity: Float
)

data class PredictionResult(
    val primaryClass: DiagnosisClass,
    val confidence: Float,
    val probabilities: Map<DiagnosisClass, Float>,
    val riskLevel: String,
    val riskColor: Color,
    val ckdStage: String,
    val recommendations: List<String>,
    val heatmapRegion: HeatmapRegion,
    val inferenceTimeMs: Long,
    val modelUsed: ModelArchitecture
)

data class BenchmarkCase(
    val caseId: String,
    val title: String,
    val patientAge: Int,
    val patientGender: String,
    val scanType: String,
    val groundTruth: DiagnosisClass,
    val clinicalPresentation: String,
    val egfr: Double,
    val serumCreatinine: Double,
    val bun: Double,
    val urineProtein: String,
    val systolicBp: Int,
    val diastolicBp: Int,
    val symptoms: String,
    val defaultProbabilities: Map<DiagnosisClass, Float>,
    val sampleImageRes: Int? = null,
    val heatmapCenter: Pair<Float, Float> = Pair(0.62f, 0.48f)
)

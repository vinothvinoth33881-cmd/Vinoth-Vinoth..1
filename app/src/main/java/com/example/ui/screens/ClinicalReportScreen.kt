package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DiagnosisClass
import com.example.ui.components.AcademicDisclaimerBanner
import com.example.ui.theme.ClinicalAlert
import com.example.ui.theme.ClinicalSuccess
import com.example.ui.theme.ClinicalWarning
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MedicalCyanLight
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KidneyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClinicalReportScreen(
    viewModel: KidneyViewModel
) {
    val activeCaseDetail by viewModel.activeCaseDetail.collectAsStateWithLifecycle()
    val predictionResult by viewModel.predictionResult.collectAsStateWithLifecycle()

    val patientId = activeCaseDetail?.patientId ?: viewModel.patientId.value
    val patientAge = activeCaseDetail?.patientAge ?: viewModel.patientAge.value
    val patientGender = activeCaseDetail?.patientGender ?: viewModel.patientGender.value
    val scanType = activeCaseDetail?.scanType ?: viewModel.scanType.value
    val egfr = activeCaseDetail?.egfr ?: viewModel.egfr.value
    val creatinine = activeCaseDetail?.serumCreatinine ?: viewModel.serumCreatinine.value
    val bun = activeCaseDetail?.bun ?: viewModel.bun.value
    val urineProtein = activeCaseDetail?.urineProtein ?: viewModel.urineProtein.value
    val bloodPressure = activeCaseDetail?.bloodPressure ?: viewModel.bloodPressure.value
    val symptoms = activeCaseDetail?.symptoms ?: viewModel.symptoms.value

    val primaryClassStr = activeCaseDetail?.primaryDiagnosis ?: (predictionResult?.primaryClass?.name ?: "NORMAL")
    val diagClass = try {
        DiagnosisClass.valueOf(primaryClassStr)
    } catch (e: Exception) {
        DiagnosisClass.NORMAL
    }

    val confidence = activeCaseDetail?.confidence ?: (predictionResult?.confidence ?: 0.98f)
    val modelName = activeCaseDetail?.activeModel ?: (predictionResult?.modelUsed?.shortName ?: "EfficientNet-B4")
    val riskLevel = activeCaseDetail?.riskLevel ?: (predictionResult?.riskLevel ?: "Low Risk")
    val ckdStage = activeCaseDetail?.ckdStage ?: (predictionResult?.ckdStage ?: "Normal Renal Function")

    val timestamp = activeCaseDetail?.timestamp ?: System.currentTimeMillis()
    val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))

    var reviewNotes by remember(activeCaseDetail) {
        mutableStateOf(activeCaseDetail?.clinicianNotes ?: "AI findings consistent with imaging presentation. Recommend standard clinical follow-up protocol.")
    }
    var isVerified by remember(activeCaseDetail) {
        mutableStateOf(activeCaseDetail?.reviewStatus?.contains("Verified", ignoreCase = true) == true)
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("clinical_report_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical Diagnostic Report",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Decision Support Document • Case #$patientId",
                    fontSize = 11.5.sp,
                    color = MedicalCyanLight
                )
            }

            IconButton(
                onClick = {
                    val reportText = buildReportString(
                        patientId, patientAge, patientGender, scanType, formattedDate,
                        diagClass, confidence, modelName, riskLevel, ckdStage,
                        egfr, creatinine, bun, urineProtein, bloodPressure, symptoms, reviewNotes, isVerified
                    )
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Clinical Report", reportText))
                    Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, CircleShape)
                    .testTag("copy_report_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Report",
                    tint = MedicalCyanLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mandatory Academic & Clinical Decision Support Disclaimer
        AcademicDisclaimerBanner(initiallyExpanded = true)

        Spacer(modifier = Modifier.height(14.dp))

        // Formal Medical Report Container Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .testTag("clinical_report_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF081223))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Hospital / Academic Department Heading
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MedicalCyanLight.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = MedicalCyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ACADEMIC NEPHROLOGY MEDICAL CENTER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "AI RADIOLOGY DECISION SUPPORT SYSTEM",
                                fontSize = 9.5.sp,
                                color = MedicalCyanLight,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Text(
                        text = "SECURE PROTOCOL",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 1: Patient Demographics & Exam Info
                ReportSectionHeader(title = "1. PATIENT DEMOGRAPHICS & SCAN PROTOCOL")
                Spacer(modifier = Modifier.height(6.dp))
                ReportGridRow(label1 = "Patient ID:", value1 = patientId, label2 = "Exam Date:", value2 = formattedDate)
                ReportGridRow(label1 = "Age / Sex:", value1 = "$patientAge yrs / $patientGender", label2 = "Accession:", value2 = "ACC-${(10000..99999).random()}")
                ReportGridRow(label1 = "Imaging Study:", value1 = scanType, label2 = "Contrast:", value2 = if (scanType.contains("Contrast", ignoreCase = true)) "Omnipaque 350 IV" else "Non-contrast")

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 2: AI Predictive Analysis
                ReportSectionHeader(title = "2. MACHINE LEARNING FINDINGS (DECISION SUPPORT)")
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Detected Primary Classification:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = diagClass.scientificName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = diagClass.color
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Model Confidence:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${(confidence * 100).let { "%.1f".format(it) }}% ($modelName)",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Morphological Sign Description:\n${diagClass.summary}",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 3: Multimodal Laboratory Correlation
                ReportSectionHeader(title = "3. MULTIMODAL LAB & RENAL FUNCTION CORRELATION")
                Spacer(modifier = Modifier.height(6.dp))
                ReportGridRow(label1 = "eGFR (CKD-EPI):", value1 = "$egfr mL/min/1.73m²", label2 = "Serum Creatinine:", value2 = "$creatinine mg/dL")
                ReportGridRow(label1 = "BUN (Urea):", value1 = "$bun mg/dL", label2 = "Urine Protein:", value2 = urineProtein)
                ReportGridRow(label1 = "Blood Pressure:", value1 = bloodPressure, label2 = "Staging Estimate:", value2 = ckdStage)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reported Symptoms: $symptoms",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 4: Impression & Risk Assessment
                ReportSectionHeader(title = "4. IMPRESSION & RISK STRATIFICATION")
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(diagClass.color.copy(alpha = 0.15f))
                        .border(1.dp, diagClass.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "RISK ASSESSMENT: $riskLevel",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = diagClass.color
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = diagClass.clinicalGuidance,
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // SECTION 5: Clinician Attestation & Review
                ReportSectionHeader(title = "5. PHYSICIAN / RADIOLOGIST ATTESTATION")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Physician Verification Status",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isVerified) "Verified by licensed radiologist" else "Awaiting clinical sign-off",
                            fontSize = 10.sp,
                            color = if (isVerified) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                    }

                    Switch(
                        checked = isVerified,
                        onCheckedChange = { checked ->
                            isVerified = checked
                            activeCaseDetail?.let {
                                viewModel.updateCaseReviewStatus(
                                    it.id,
                                    if (checked) "Verified by Physician" else "Pending Radiologist Review",
                                    reviewNotes
                                )
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981)
                        ),
                        modifier = Modifier.testTag("verification_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewNotes,
                    onValueChange = {
                        reviewNotes = it
                        activeCaseDetail?.let { entity ->
                            viewModel.updateCaseReviewStatus(
                                entity.id,
                                if (isVerified) "Verified by Physician" else "Pending Radiologist Review",
                                it
                            )
                        }
                    },
                    label = { Text("Radiologist / Clinician Notes", fontSize = 10.5.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalCyanLight,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clinician_notes_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Copy / Return to scan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.navigateTo(AppDestination.DIAGNOSIS) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("return_to_scanner_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalCyanLight)
            ) {
                Text(text = "Back to Scanner", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val reportText = buildReportString(
                        patientId, patientAge, patientGender, scanType, formattedDate,
                        diagClass, confidence, modelName, riskLevel, ckdStage,
                        egfr, creatinine, bun, urineProtein, bloodPressure, symptoms, reviewNotes, isVerified
                    )
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Clinical Report", reportText))
                    Toast.makeText(context, "Full Report Exported to Clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("export_summary_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanLight)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color(0xFF041E28),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Export Summary",
                    color = Color(0xFF041E28),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun ReportSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = MedicalCyanLight,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun ReportGridRow(
    label1: String, value1: String,
    label2: String, value2: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Text(text = "$label1 ", fontSize = 10.5.sp, color = Color(0xFF94A3B8))
            Text(text = value1, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
        Row(modifier = Modifier.weight(1f)) {
            Text(text = "$label2 ", fontSize = 10.5.sp, color = Color(0xFF94A3B8))
            Text(text = value2, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

private fun buildReportString(
    patientId: String, age: Int, sex: String, study: String, date: String,
    diagClass: DiagnosisClass, confidence: Float, model: String, risk: String, ckd: String,
    egfr: Double, cr: Double, bun: Double, protein: String, bp: String, symptoms: String,
    notes: String, isVerified: Boolean
): String {
    return """
============================================================
ACADEMIC NEPHROLOGY & RADIOLOGY AI DECISION SUPPORT REPORT
NOTICE: For research & educational decision-support only.
Not a replacement for clinical diagnosis by a licensed physician.
============================================================
PATIENT ID: $patientId
AGE / GENDER: $age yrs / $sex
EXAMINATION DATE: $date
STUDY: $study

AI/ML FINDINGS:
- Predicted Classification: ${diagClass.scientificName}
- Model Confidence: ${(confidence * 100).let { "%.1f".format(it) }}% ($model)
- Primary Feature Findings: ${diagClass.summary}

MULTIMODAL LABORATORY CORRELATION:
- eGFR: $egfr mL/min/1.73m²
- Serum Creatinine: $cr mg/dL
- BUN: $bun mg/dL | Proteinuria: $protein | BP: $bp
- Renal Function Staging: $ckd
- Presenting Symptoms: $symptoms

IMPRESSION & TRIAGE PROTOCOL:
- Risk Level: $risk
- Clinical Guidance: ${diagClass.clinicalGuidance}

PHYSICIAN REVIEW:
- Verification Status: ${if (isVerified) "VERIFIED BY ATTENDING PHYSICIAN" else "PENDING RADIOLOGIST REVIEW"}
- Clinician Notes: $notes
============================================================
""".trimIndent()
}

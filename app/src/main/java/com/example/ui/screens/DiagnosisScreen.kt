package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.BenchmarkCase
import com.example.model.DiagnosisClass
import com.example.model.ModelArchitecture
import com.example.ui.components.AcademicDisclaimerBanner
import com.example.ui.components.DicomWindowViewer
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MedicalCyanLight
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KidneyViewModel

@Composable
fun DiagnosisScreen(
    viewModel: KidneyViewModel,
    onOpenGuide: () -> Unit
) {
    val selectedBenchmark by viewModel.selectedBenchmark.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val predictionResult by viewModel.predictionResult.collectAsStateWithLifecycle()
    val saveMessage by viewModel.savedCaseSuccessMessage.collectAsStateWithLifecycle()

    val patientId by viewModel.patientId.collectAsStateWithLifecycle()
    val patientAge by viewModel.patientAge.collectAsStateWithLifecycle()
    val egfr by viewModel.egfr.collectAsStateWithLifecycle()
    val creatinine by viewModel.serumCreatinine.collectAsStateWithLifecycle()
    val bun by viewModel.bun.collectAsStateWithLifecycle()
    val urineProtein by viewModel.urineProtein.collectAsStateWithLifecycle()
    val symptoms by viewModel.symptoms.collectAsStateWithLifecycle()
    val bloodPressure by viewModel.bloodPressure.collectAsStateWithLifecycle()

    var showClinicalInputs by remember { mutableStateOf(false) }

    // Photo picker launcher for user scan images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.selectUserImage(it) }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("diagnosis_screen")
    ) {
        // App Header Bar with Guide Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NephroScan AI",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Medical Image Diagnosis for Kidney Disease",
                    fontSize = 12.sp,
                    color = MedicalCyanLight,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = onOpenGuide,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, CircleShape)
                    .testTag("open_guide_button")
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Kidney Disease Atlas Guide",
                    tint = MedicalCyanLight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hero Medical Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.kidney_diag_banner),
                contentDescription = "Radiology AI Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x990A1128))
                    .padding(14.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = "ACADEMIC ML BENCHMARK SYSTEM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Deep CNN Multiclass Analysis (Normal, Cyst, Stone, Tumor)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Prominent Academic Disclaimer Banner (Mandatory requirement)
        AcademicDisclaimerBanner()

        Spacer(modifier = Modifier.height(14.dp))

        // Scan Selection Header: Benchmark Cases & Upload Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1. SELECT CT SCAN DATASET",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalCyanLight,
                letterSpacing = 0.5.sp
            )

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .height(34.dp)
                    .testTag("upload_scan_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalCyanLight)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Upload Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Benchmark Cases Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.benchmarkCases.forEach { bCase ->
                val isSelected = selectedBenchmark?.caseId == bCase.caseId && userImageUri == null
                BenchmarkCaseCard(
                    caseItem = bCase,
                    isSelected = isSelected,
                    onClick = { viewModel.selectBenchmarkCase(bCase) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // DICOM Inspector Viewer with Grad-CAM Heatmap
        Text(
            text = "2. INTERACTIVE DICOM VIEWER & GRAD-CAM",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = MedicalCyanLight,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        DicomWindowViewer(
            imageUri = userImageUri,
            sampleImageRes = selectedBenchmark?.sampleImageRes ?: R.drawable.sample_kidney_ct,
            patientId = patientId,
            scanType = viewModel.scanType.value,
            heatmapRegion = predictionResult?.heatmapRegion
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Model Architecture Selection
        Text(
            text = "3. DEEP LEARNING MODEL ARCHITECTURE",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = MedicalCyanLight,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModelArchitecture.values().forEach { model ->
                val isSelected = selectedModel == model
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MedicalCyanLight.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isSelected) MedicalCyanLight else DarkBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setModel(model) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("model_select_${model.shortName.lowercase()}")
                ) {
                    Column {
                        Text(
                            text = model.shortName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MedicalCyanLight else Color.White
                        )
                        Text(
                            text = "Acc: ${(model.benchmarkAccuracy * 100).let { "%.1f".format(it) }}% | ~${model.latencyMs}ms",
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Multimodal Patient Clinical Labs (Collapsible)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                .clickable { showClinicalInputs = !showClinicalInputs }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = MedicalCyanLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Multimodal Lab & Clinical Profile",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "eGFR: $egfr mL/min | Cr: $creatinine mg/dL | BUN: $bun",
                        fontSize = 10.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Icon(
                imageVector = if (showClinicalInputs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = showClinicalInputs,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF091222))
                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = egfr.toString(),
                        onValueChange = { it.toDoubleOrNull()?.let { v -> viewModel.egfr.value = v } },
                        label = { Text("eGFR (mL/min)", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalCyanLight,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = creatinine.toString(),
                        onValueChange = { it.toDoubleOrNull()?.let { v -> viewModel.serumCreatinine.value = v } },
                        label = { Text("Creatinine (mg/dL)", fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalCyanLight,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { viewModel.symptoms.value = it },
                    label = { Text("Presenting Symptoms", fontSize = 10.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalCyanLight,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CTA: Run AI Inference Button
        Button(
            onClick = { viewModel.runDiagnosis() },
            enabled = !isAnalyzing,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("run_inference_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanLight)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    color = Color(0xFF041E28),
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Running Deep Neural Network Inference...",
                    color = Color(0xFF041E28),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF041E28),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RUN AI DIAGNOSTIC INFERENCE",
                    color = Color(0xFF041E28),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification alert if case saved
        saveMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF064E3B))
                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Diagnostic Prediction Results Card
        predictionResult?.let { result ->
            DiagnosticResultCard(
                result = result,
                onSaveCase = { viewModel.saveCurrentCase() },
                onViewFullReport = { viewModel.navigateTo(AppDestination.REPORT) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun BenchmarkCaseCard(
    caseItem: BenchmarkCase,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) MedicalCyanLight else DarkBorder
    val bgColor = if (isSelected) Color(0xFF0A2239) else DarkSurfaceVariant

    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("benchmark_card_${caseItem.caseId.lowercase()}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = caseItem.caseId,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanLight
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(caseItem.groundTruth.color.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = caseItem.groundTruth.displayName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = caseItem.groundTruth.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = caseItem.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${caseItem.patientAge}y ${caseItem.patientGender} | eGFR ${caseItem.egfr.toInt()}",
                fontSize = 9.5.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun DiagnosticResultCard(
    result: com.example.model.PredictionResult,
    onSaveCase: () -> Unit,
    onViewFullReport: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, result.primaryClass.color.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .testTag("diagnostic_results_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF091426))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header: Prediction & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRIMARY AI/ML PREDICTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = result.primaryClass.scientificName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = result.primaryClass.color
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(result.confidence * 100).let { "%.1f".format(it) }}%",
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = result.primaryClass.color
                    )
                    Text(
                        text = "Confidence",
                        fontSize = 9.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Multimodal Risk Alert Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(result.riskColor.copy(alpha = 0.15f))
                    .border(1.dp, result.riskColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = result.riskLevel,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = result.riskColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Probability Distribution Bars for all 4 classes
            Text(
                text = "SOFTMAX CLASS PROBABILITIES (${result.modelUsed.shortName}):",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(6.dp))

            DiagnosisClass.values().forEach { dClass ->
                val prob = result.probabilities[dClass] ?: 0f
                Column(modifier = Modifier.padding(vertical = 3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dClass.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (dClass == result.primaryClass) FontWeight.Bold else FontWeight.Normal,
                            color = if (dClass == result.primaryClass) Color.White else Color(0xFF94A3B8)
                        )
                        Text(
                            text = "${(prob * 100).let { "%.1f".format(it) }}%",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = dClass.color
                        )
                    }
                    LinearProgressIndicator(
                        progress = { prob },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp)),
                        color = dClass.color,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CKD Staging Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MedicalCyanLight)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Renal Function Correlation: ${result.ckdStage}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveCase,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("save_case_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalCyanLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save Record", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onViewFullReport,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("view_report_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF041E28),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Clinical Report",
                        color = Color(0xFF041E28),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

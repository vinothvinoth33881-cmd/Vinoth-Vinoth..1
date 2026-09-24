package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiagnosisClass
import com.example.ui.components.AcademicConfusionMatrix
import com.example.ui.components.AcademicDisclaimerBanner
import com.example.ui.components.ModelEvaluationComparisonTable
import com.example.ui.components.RocCurvesAndLossCanvas
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MedicalCyanLight

@Composable
fun BenchmarksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("benchmarks_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Model Evaluation & Benchmarks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Kidney Disease Deep Learning Architecture Metrics",
                    fontSize = 11.5.sp,
                    color = MedicalCyanLight
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Disclaimer
        AcademicDisclaimerBanner()

        Spacer(modifier = Modifier.height(14.dp))

        // Key Performance KPI Cards (4 metrics)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiMetricBox(
                modifier = Modifier.weight(1f),
                label = "Accuracy",
                value = "99.14%",
                color = Color(0xFF10B981)
            )
            KpiMetricBox(
                modifier = Modifier.weight(1f),
                label = "Macro F1",
                value = "99.02%",
                color = Color(0xFF38BDF8)
            )
            KpiMetricBox(
                modifier = Modifier.weight(1f),
                label = "Sensitivity",
                value = "99.18%",
                color = Color(0xFFA855F7)
            )
            KpiMetricBox(
                modifier = Modifier.weight(1f),
                label = "AUC-ROC",
                value = "0.998",
                color = Color(0xFFF59E0B)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4x4 Confusion Matrix
        AcademicConfusionMatrix()

        Spacer(modifier = Modifier.height(14.dp))

        // ROC Curves Canvas
        RocCurvesAndLossCanvas()

        Spacer(modifier = Modifier.height(14.dp))

        // Model Architecture Benchmarks (ResNet-50 vs EfficientNet vs VGG vs Ensemble)
        ModelEvaluationComparisonTable()

        Spacer(modifier = Modifier.height(14.dp))

        // Dataset Distribution Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dataset,
                        contentDescription = null,
                        tint = MedicalCyanLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BENCHMARK DATASET COMPOSITION (N = 12,446)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanLight,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatasetClassChip(
                        modifier = Modifier.weight(1f),
                        name = "Normal",
                        count = "3,450",
                        pct = "27.7%",
                        color = ColorNormal
                    )
                    DatasetClassChip(
                        modifier = Modifier.weight(1f),
                        name = "Cyst",
                        count = "3,709",
                        pct = "29.8%",
                        color = ColorCyst
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatasetClassChip(
                        modifier = Modifier.weight(1f),
                        name = "Stone",
                        count = "1,377",
                        pct = "11.1%",
                        color = ColorStone
                    )
                    DatasetClassChip(
                        modifier = Modifier.weight(1f),
                        name = "Tumor",
                        count = "3,910",
                        pct = "31.4%",
                        color = ColorTumor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Data split: 70% Training (8,712) / 15% Validation (1,867) / 15% Hold-out Testing (1,867). " +
                            "Normalized across HU window ranges (-100 to +900 HU) with spatial data augmentation.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun KpiMetricBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 9.5.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}

@Composable
private fun DatasetClassChip(
    modifier: Modifier = Modifier,
    name: String,
    count: String,
    pct: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F1A2E))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "$count slices",
                    fontSize = 9.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }
            Text(
                text = pct,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

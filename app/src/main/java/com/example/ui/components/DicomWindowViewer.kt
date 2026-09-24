package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.HeatmapRegion
import com.example.model.ScanWindowPreset
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MedicalCyanLight

@Composable
fun DicomWindowViewer(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    sampleImageRes: Int? = R.drawable.sample_kidney_ct,
    patientId: String = "PT-84920",
    scanType: String = "Axial CT",
    heatmapRegion: HeatmapRegion? = null,
    showHeatmapDefault: Boolean = true
) {
    var activePreset by remember { mutableStateOf(ScanWindowPreset.STANDARD) }
    var isInverted by remember { mutableStateOf(false) }
    var showHeatmap by remember { mutableStateOf(showHeatmapDefault) }
    var heatmapOpacity by remember { mutableFloatStateOf(0.65f) }

    // Color matrix calculation based on window level preset and invert flag
    val colorMatrix = remember(activePreset, isInverted) {
        val contrastScale = when (activePreset) {
            ScanWindowPreset.STANDARD -> 1.0f
            ScanWindowPreset.SOFT_TISSUE -> 1.25f
            ScanWindowPreset.BONE_CALCULI -> 1.65f
            ScanWindowPreset.RENAL_CORTEX -> 1.40f
        }
        val brightnessShift = when (activePreset) {
            ScanWindowPreset.STANDARD -> 0f
            ScanWindowPreset.SOFT_TISSUE -> 15f
            ScanWindowPreset.BONE_CALCULI -> -25f
            ScanWindowPreset.RENAL_CORTEX -> 20f
        }

        val cm = ColorMatrix()
        if (isInverted) {
            // Invert colors
            val invertArray = floatArrayOf(
                -contrastScale, 0f, 0f, 0f, 255f + brightnessShift,
                0f, -contrastScale, 0f, 0f, 255f + brightnessShift,
                0f, 0f, -contrastScale, 0f, 255f + brightnessShift,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(invertArray))
        } else {
            val normalArray = floatArrayOf(
                contrastScale, 0f, 0f, 0f, brightnessShift,
                0f, contrastScale, 0f, 0f, brightnessShift,
                0f, 0f, contrastScale, 0f, brightnessShift,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(normalArray))
        }
        cm
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF060D1A))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("dicom_window_viewer")
    ) {
        // Radiology Workstation Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "DICOM / CT INSPECTOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanLight,
                    letterSpacing = 1.sp
                )
            }

            Text(
                text = "$scanType | W:${activePreset.windowWidth} L:${activePreset.windowLevel}",
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CT Viewport Box with Crosshairs & Grad-CAM Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black)
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Main CT Image Render
            val context = LocalContext.current
            val imageModel = imageUri ?: (sampleImageRes ?: R.drawable.sample_kidney_ct)

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = "Kidney CT Scan Slice",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )

            // Grad-CAM Heatmap Overlay Canvas
            if (showHeatmap && heatmapRegion != null) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("gradcam_heatmap_canvas")
                ) {
                    val cx = size.width * heatmapRegion.normalizedCenterX
                    val cy = size.height * heatmapRegion.normalizedCenterY
                    val radius = size.width * heatmapRegion.radiusRatio

                    // Medical saliency gradient (Blue -> Cyan -> Lime -> Yellow -> Deep Red)
                    val heatColors = listOf(
                        Color(0xFFFF0055).copy(alpha = heatmapOpacity * heatmapRegion.peakIntensity),
                        Color(0xFFFF7A00).copy(alpha = heatmapOpacity * 0.85f),
                        Color(0xFFFFDD00).copy(alpha = heatmapOpacity * 0.60f),
                        Color(0xFF00FFB2).copy(alpha = heatmapOpacity * 0.35f),
                        Color(0xFF0066FF).copy(alpha = heatmapOpacity * 0.15f),
                        Color.Transparent
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = heatColors,
                            center = Offset(cx, cy),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(cx, cy),
                        blendMode = BlendMode.Screen
                    )

                    // Diagnostic Bounding Reticle
                    drawCircle(
                        color = Color(0xFFFF2A6D).copy(alpha = 0.85f),
                        radius = radius * 0.75f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Target crosshair ticks
                    val tickLen = 14.dp.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = Offset(cx - tickLen, cy),
                        end = Offset(cx + tickLen, cy),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = Offset(cx, cy - tickLen),
                        end = Offset(cx, cy + tickLen),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
            }

            // HUD Annotations (Corner Radiologist Info)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ID: $patientId\nKV: 120 | mA: 240\nFOV: 350mm",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Cyan.copy(alpha = 0.85f),
                        lineHeight = 12.sp
                    )

                    Text(
                        text = "AI ATTN: ${if (showHeatmap) "GRAD-CAM ACTIVE" else "OFF"}\nCONF: CALIBRATED\nAXIAL SLICE #48",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (showHeatmap) Color(0xFFFF4081) else Color.White.copy(alpha = 0.6f),
                        lineHeight = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "R ◀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "▶ L",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tooling Row: Window Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ScanWindowPreset.values().forEach { preset ->
                val isSelected = activePreset == preset
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MedicalCyanLight.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MedicalCyanLight else DarkBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { activePreset = preset }
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = preset.title,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MedicalCyanLight else Color(0xFFCBD5E1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Saliency Heatmap & Polarity Invert Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showHeatmap = !showHeatmap },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showHeatmap) Color(0xFFFF4081).copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (showHeatmap) Color(0xFFFF4081) else DarkBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .testTag("toggle_heatmap_button")
                ) {
                    Icon(
                        imageVector = if (showHeatmap) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Grad-CAM Heatmap",
                        tint = if (showHeatmap) Color(0xFFFF4081) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { isInverted = !isInverted },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isInverted) MedicalCyanLight.copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isInverted) MedicalCyanLight else DarkBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .testTag("toggle_invert_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = "Invert Polarity",
                        tint = if (isInverted) MedicalCyanLight else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Grad-CAM Heatmap",
                    fontSize = 11.sp,
                    color = if (showHeatmap) Color(0xFFFF80AB) else Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }

            if (showHeatmap) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(130.dp)
                ) {
                    Text(
                        text = "${(heatmapOpacity * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Slider(
                        value = heatmapOpacity,
                        onValueChange = { heatmapOpacity = it },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF4081),
                            activeTrackColor = Color(0xFFFF4081),
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

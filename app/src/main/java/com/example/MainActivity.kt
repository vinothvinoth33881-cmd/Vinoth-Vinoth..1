package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BenchmarksScreen
import com.example.ui.screens.CasesHistoryScreen
import com.example.ui.screens.ClinicalReportScreen
import com.example.ui.screens.DiagnosisScreen
import com.example.ui.screens.EducationalGuideDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MedicalCyanLight
import com.example.ui.theme.NephroScanTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.KidneyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: KidneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NephroScanTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: KidneyViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    var showEducationalGuide by remember { mutableStateOf(false) }

    if (showEducationalGuide) {
        EducationalGuideDialog(
            onDismiss = { showEducationalGuide = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = currentDestination == AppDestination.DIAGNOSIS,
                    onClick = { viewModel.navigateTo(AppDestination.DIAGNOSIS) },
                    icon = {
                        Icon(
                            imageVector = if (currentDestination == AppDestination.DIAGNOSIS)
                                Icons.Filled.Psychology else Icons.Outlined.Psychology,
                            contentDescription = "Diagnosis"
                        )
                    },
                    label = { Text("Diagnosis", fontSize = 10.5.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF041E28),
                        selectedTextColor = MedicalCyanLight,
                        indicatorColor = MedicalCyanLight,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_diagnosis")
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.CASES,
                    onClick = { viewModel.navigateTo(AppDestination.CASES) },
                    icon = {
                        Icon(
                            imageVector = if (currentDestination == AppDestination.CASES)
                                Icons.Filled.FolderShared else Icons.Outlined.FolderShared,
                            contentDescription = "Cases History"
                        )
                    },
                    label = { Text("Cases", fontSize = 10.5.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF041E28),
                        selectedTextColor = MedicalCyanLight,
                        indicatorColor = MedicalCyanLight,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_cases")
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.BENCHMARKS,
                    onClick = { viewModel.navigateTo(AppDestination.BENCHMARKS) },
                    icon = {
                        Icon(
                            imageVector = if (currentDestination == AppDestination.BENCHMARKS)
                                Icons.Filled.Assessment else Icons.Outlined.Assessment,
                            contentDescription = "Benchmarks"
                        )
                    },
                    label = { Text("Metrics", fontSize = 10.5.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF041E28),
                        selectedTextColor = MedicalCyanLight,
                        indicatorColor = MedicalCyanLight,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_benchmarks")
                )

                NavigationBarItem(
                    selected = currentDestination == AppDestination.REPORT,
                    onClick = { viewModel.navigateTo(AppDestination.REPORT) },
                    icon = {
                        Icon(
                            imageVector = if (currentDestination == AppDestination.REPORT)
                                Icons.Filled.Description else Icons.Outlined.Description,
                            contentDescription = "Clinical Report"
                        )
                    },
                    label = { Text("Report", fontSize = 10.5.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF041E28),
                        selectedTextColor = MedicalCyanLight,
                        indicatorColor = MedicalCyanLight,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_report")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentDestination,
                label = "ScreenTransition"
            ) { destination ->
                when (destination) {
                    AppDestination.DIAGNOSIS -> DiagnosisScreen(
                        viewModel = viewModel,
                        onOpenGuide = { showEducationalGuide = true }
                    )
                    AppDestination.CASES -> CasesHistoryScreen(
                        viewModel = viewModel
                    )
                    AppDestination.BENCHMARKS -> BenchmarksScreen()
                    AppDestination.REPORT -> ClinicalReportScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

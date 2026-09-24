package com.example

import com.example.ml.KidneyInferenceEngine
import com.example.model.BenchmarkDataset
import com.example.model.DiagnosisClass
import com.example.model.ModelArchitecture
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBenchmarkCasesPresent() {
        val cases = BenchmarkDataset.sampleCases
        assertEquals(4, cases.size)
        assertTrue(cases.any { it.groundTruth == DiagnosisClass.TUMOR })
        assertTrue(cases.any { it.groundTruth == DiagnosisClass.STONE })
        assertTrue(cases.any { it.groundTruth == DiagnosisClass.CYST })
        assertTrue(cases.any { it.groundTruth == DiagnosisClass.NORMAL })
    }

    @Test
    fun testInferenceEngineBenchmarkAnalysis() = runBlocking {
        val tumorCase = BenchmarkDataset.sampleCases.first { it.groundTruth == DiagnosisClass.TUMOR }
        val result = KidneyInferenceEngine.analyzeScan(
            bitmap = null,
            selectedBenchmark = tumorCase,
            model = ModelArchitecture.EFFICIENTNET_B4,
            egfr = tumorCase.egfr,
            creatinine = tumorCase.serumCreatinine,
            bun = tumorCase.bun,
            urineProtein = tumorCase.urineProtein,
            symptoms = tumorCase.symptoms
        )

        assertEquals(DiagnosisClass.TUMOR, result.primaryClass)
        assertTrue(result.confidence > 0.85f)
        assertNotNull(result.heatmapRegion)
        assertTrue(result.recommendations.isNotEmpty())
    }
}

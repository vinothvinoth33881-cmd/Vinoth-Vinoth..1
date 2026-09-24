package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kidney_diagnoses")
data class KidneyDiagnosisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val patientAge: Int,
    val patientGender: String,
    val scanType: String,
    val imagePathOrUri: String,
    val primaryDiagnosis: String, // NORMAL, CYST, STONE, TUMOR
    val confidence: Float,
    val probNormal: Float,
    val probCyst: Float,
    val probStone: Float,
    val probTumor: Float,
    val activeModel: String,
    val egfr: Double,
    val serumCreatinine: Double,
    val bun: Double,
    val urineProtein: String,
    val bloodPressure: String,
    val symptoms: String,
    val riskLevel: String,
    val ckdStage: String,
    val reviewStatus: String, // "Pending Radiologist Review", "Verified by Physician", "Discrepancy / Follow-up"
    val clinicianNotes: String,
    val timestamp: Long = System.currentTimeMillis()
)

package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KidneyDiagnosisDao {
    @Query("SELECT * FROM kidney_diagnoses ORDER BY timestamp DESC")
    fun getAllDiagnoses(): Flow<List<KidneyDiagnosisEntity>>

    @Query("SELECT * FROM kidney_diagnoses WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisById(id: Long): KidneyDiagnosisEntity?

    @Query("SELECT * FROM kidney_diagnoses WHERE primaryDiagnosis = :diagnosisClass ORDER BY timestamp DESC")
    fun getDiagnosesByClass(diagnosisClass: String): Flow<List<KidneyDiagnosisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: KidneyDiagnosisEntity): Long

    @Update
    suspend fun updateDiagnosis(diagnosis: KidneyDiagnosisEntity)

    @Query("UPDATE kidney_diagnoses SET reviewStatus = :newStatus, clinicianNotes = :notes WHERE id = :id")
    suspend fun updateReviewStatus(id: Long, newStatus: String, notes: String)

    @Query("DELETE FROM kidney_diagnoses WHERE id = :id")
    suspend fun deleteDiagnosisById(id: Long)

    @Query("DELETE FROM kidney_diagnoses")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM kidney_diagnoses")
    suspend fun getCount(): Int
}

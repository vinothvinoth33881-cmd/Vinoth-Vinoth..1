package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [KidneyDiagnosisEntity::class], version = 1, exportSchema = false)
abstract class KidneyDatabase : RoomDatabase() {
    abstract fun kidneyDiagnosisDao(): KidneyDiagnosisDao

    companion object {
        @Volatile
        private var INSTANCE: KidneyDatabase? = null

        fun getInstance(context: Context): KidneyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KidneyDatabase::class.java,
                    "kidney_diagnosis_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

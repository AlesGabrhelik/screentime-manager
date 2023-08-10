package com.example.screentime_manager

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SelectedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: SelectedApp)

    @Query("SELECT * FROM selected_apps")
    suspend fun getAllSelectedApps(): List<SelectedApp>

    @Query("DELETE FROM selected_apps WHERE packageName = :packageName")
    suspend fun deleteSelectedApp(packageName: String)
}
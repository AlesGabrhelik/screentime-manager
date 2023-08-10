package com.example.screentime_manager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_apps")
data class SelectedApp(
    @PrimaryKey val packageName: String
)
package com.tk.a12testers14days.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val appName: String,
    val playStoreLink: String,
    val appIcon: String?,
    val status: String,
    val dayCount: Int,
    val developerName: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

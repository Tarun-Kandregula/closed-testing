package com.tk.a12testers14days.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MissionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun missionDao(): MissionDao
}

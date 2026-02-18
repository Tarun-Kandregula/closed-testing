package com.tk.a12testers14days.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions")
    suspend fun getAllMissions(): List<MissionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Query("DELETE FROM missions")
    suspend fun clearMissions()
}

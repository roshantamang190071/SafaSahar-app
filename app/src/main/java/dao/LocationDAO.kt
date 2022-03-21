package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import entity.Location

@Dao
interface LocationDAO {

    @Insert
    suspend fun addLocation(location : Location)

    @Query("select * from Location")
    suspend fun showAllLocation():List<Location>

}
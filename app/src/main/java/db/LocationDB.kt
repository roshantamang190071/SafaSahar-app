package db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dao.LocationDAO
import entity.Location

@Database(
    entities = [(Location::class)],
    version = 1,
    exportSchema = false
)
abstract class LocationDB : RoomDatabase() {

    abstract fun getLocationDAO(): LocationDAO

    companion object {
        @Volatile
        private var instance: LocationDB? = null

        fun getInstance(context: Context): LocationDB {
            if (instance == null) {
                synchronized(LocationDB::class) {
                    instance = buildDatabase(context)
                }
            }
            return instance!!
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                LocationDB::class.java,
                "LocationDB"
            ).build()
    }
}
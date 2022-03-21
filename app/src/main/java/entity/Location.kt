package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location (
    @PrimaryKey
    var LocationId : String = "",
    var latitude : String? = null,
    var longitude : String? = null,
    var userId : String? = null,
    var date : String? = null,
    var address :String? = null
)
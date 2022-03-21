package entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (

    var fullName : String? = null,
    var password : String? = null,
    var phoneNumber : String? = null,
    var profile_pic : String? =null
){
    @PrimaryKey
    var userId : String = ""
}
package model

data class UserModel (

    val _id : String = "",
    var fullName : String? = null,
    var phoneNumber : String? = null,
    var password : String? = null,
    var profilePic :String? = null
)
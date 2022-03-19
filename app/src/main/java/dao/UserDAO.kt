package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import entity.User

@Dao
interface UserDAO {

    @Insert
    suspend fun registerUser(user : User)

    @Query("select * from User where phoneNumber=(:phoneNumber) and password=(:password)")
    suspend fun checkUser(phoneNumber: String, password: String) : User

}
package api

import model.UserModel
import response.UserResponse
import retrofit2.Response
import retrofit2.http.*


interface UserAPI {

    @POST("register/user")
    suspend fun registerUser(
        @Body user : UserModel
    ): Response<UserResponse>

    @FormUrlEncoded
    @POST("login/user")
    suspend fun login(
        @Field("phoneNumber") phoneNumber : String,
        @Field("password") password: String
    ) : Response<UserResponse>

}
package repository

import api.MyApiRequest
import api.ServiceBuilder
import api.UserAPI
import model.UserModel
import okhttp3.MultipartBody
import response.UserResponse


class UserRepository: MyApiRequest() {

    private val userApi =
        ServiceBuilder.buildService(UserAPI::class.java)

    suspend fun registerUser(user : UserModel): UserResponse {
        return apiRequest {
            userApi.registerUser(user)
        }
    }

}
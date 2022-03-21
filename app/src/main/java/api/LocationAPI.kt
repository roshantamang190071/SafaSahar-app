package api

import model.LocationModel
import response.LocationResponse
import retrofit2.Response
import retrofit2.http.*


interface LocationAPI {

    @POST("add-location")
    suspend fun addLocation(
        @Header("Authorization") token : String,
        @Body location : LocationModel
    ): Response<LocationResponse>


}
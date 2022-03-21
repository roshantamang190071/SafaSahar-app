package repository

import api.LocationAPI
import api.MyApiRequest
import api.ServiceBuilder
import model.LocationModel
import response.LocationResponse


class LocationRepository: MyApiRequest() {

    private val locationApi =
        ServiceBuilder.buildService(LocationAPI::class.java)

    suspend fun addLocation(location : LocationModel): LocationResponse {
        return apiRequest {
            locationApi.addLocation(ServiceBuilder.token!!,location)
        }
    }

    }



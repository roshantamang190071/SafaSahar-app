package response

import model.LocationModel

data class LocationResponse (
    val success : Boolean? = null,
    val data : ArrayList<LocationModel>? = null
)
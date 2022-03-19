package api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    //for emulator
    private const val BASE_URL = "http://10.0.2.2:3000/"

    //for real device
    //private const val BASE_URL = "http://192.168.1.76:3000/"

    var token: String? = null
    var userId : String? = null

    var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    //Create retrofit instance
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    //Generic function
    fun <T> buildService(serviceType: Class<T>): T {
        return retrofitBuilder.create(serviceType)
    }

    fun loadImagePath(): String {
        val arr = BASE_URL.split("/").toTypedArray()
        return arr[0] + "/" + arr[1] + arr[2] + "/"
    }


}
package com.example.myapplication.network

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface AudioImageApi {

    @POST(UPLOAD_URL)
    suspend fun uploadFile(
        @Body body: RequestBody
    ): Response<AudioUploadResponse>


    @GET(GET_URL)
    suspend fun getImage(
        @Query("filename") filename: String
    ): Response<ResponseBody>


    companion object {
        private const val BASE_URL = "<BASE_URL>"
        private const val GET_URL = "$BASE_URL/files/images"
        private const val UPLOAD_URL = "$BASE_URL/files/audio"

        val instance: AudioImageApi by lazy {
            val retrofit: Retrofit = createRetrofit()
            retrofit.create(AudioImageApi::class.java)
        }

        private fun createRetrofit(): Retrofit {
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()
        }
    }
}

data class AudioUploadResponse(
    @Json(name = "msg")
    val msg: String,
)

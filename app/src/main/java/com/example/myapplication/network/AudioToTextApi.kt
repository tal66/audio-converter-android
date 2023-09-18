package com.example.myapplication.network

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


interface AudioToTextApi {

    @PUT
    suspend fun uploadFile(
        @Url url: String,
        @Header("Content-Type") contentType: String,
        @Body body: RequestBody
    ): Response<Unit>

    @GET("$GET_RESULT_URL/{filename}")
    suspend fun getResult(
        @Query("filename") filename: String
    ): Response<GetResultResponse>


    @POST(UPLOAD_REQ_URL)
    suspend fun uploadReq(
        @Body body: RequestBody
    ): Response<UploadResponse>

    companion object {
        const val UPLOAD_REQ_URL =
            "<UPLOAD_REQ_URL>"
        const val GET_RESULT_URL =
            "<GET_RESULT_URL>"

        val instance: AudioToTextApi by lazy {
            val retrofit: Retrofit = createRetrofit()
            retrofit.create(AudioToTextApi::class.java)
        }

        private fun createRetrofit(): Retrofit {
            Log.d("**", "AudioToTextApi createRetrofit")
            // converter
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            // logger
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // client
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("https://api.example.com/") // ignored (Base URL required)
                .client(httpClient)
                .build()
        }
    }
}

@JsonClass(generateAdapter = true)
data class GetResultResponse(
    @Json(name = "msg")
    val msg: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "text")
    val text: String?,
)

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "msg")
    val msg: String,
    @Json(name = "url")
    val url: String
)


package com.example.myapplication.network

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AudioToTextRepo(private val api: AudioToTextApi) {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val getResultJsonAdapter: JsonAdapter<GetResultResponse> = moshi.adapter(GetResultResponse::class.java)
    private val uploadResponseJsonAdapter: JsonAdapter<UploadResponse> = moshi.adapter(UploadResponse::class.java)

    suspend fun uploadRequest(filename: String): Result<UploadResponse?> {
        val cleanFilename = File(filename).name.replace(" ", "_")
        try {
            val json = "{\"filename\": \"${cleanFilename}\"}"
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val response = api.uploadReq(requestBody)
            Log.d(TAG, "response.code " + response.code().toString())

            return when {
                response.isSuccessful -> {
                    val body = response.body()
                    Log.d(TAG, "uploadRequest ${body}")
                    return Result.success(response.body())
                }
                else -> {
                    val errBody = response.errorBody()?.string()
                    Log.d(TAG, "errBody $errBody")

                    val body = uploadResponseJsonAdapter.fromJson(errBody)

                    Log.d(TAG, "errorBody $errBody")
                    Log.d(TAG, "raw ${response.raw()}")
                    Result.failure(Exception("upload req error: ${body?.msg}"))
//                    Result.failure(Exception("upload req error"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(Exception("upload req error"))
        }
    }

    suspend fun upload(url: String, body: RequestBody): Result<Unit> {
        try {
            val response = api.uploadFile(url, "audio/mpeg", body)
            Log.d(TAG, "upload response.code " + response.code().toString())

            return when {
                response.isSuccessful -> Result.success(Unit)
                else -> {
                    Result.failure(Exception("upload error"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(Exception("upload error"))
        }
    }


    suspend fun getResult(filename: String): Result<GetResultResponse?> {
        Log.d(TAG, "AudioToTextRepo getResult")
        try {
            val response = api.getResult(filename)
            Log.d(TAG, "getResult response.code " + response.code().toString())

            return when {
                response.isSuccessful -> {
                    Log.d(TAG, "AudioToTextRepo getResult body ${response.body()}")
                    Result.success(response.body())
                }
                else -> {
                    Log.d(TAG, "AudioToTextRepo getResult failure")
//                    Log.d(TAG, "${response.body()}") // null
                    val errBody = response.errorBody()?.string()
                    Log.d(TAG, "errBody $errBody")

                    val body = getResultJsonAdapter.fromJson(errBody)

                    Log.d(TAG, "errorBody $errBody")
                    Log.d(TAG, "raw ${response.raw()}")
                    Result.failure(Exception(body?.msg))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(Exception("AudioToTextRepo error"))
        }
    }

    companion object {
        const val TAG = "AudioToTextRepo"
    }
}


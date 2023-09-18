package com.example.myapplication.network

import android.util.Log
import okhttp3.RequestBody
import java.io.File


class AudioImageRepo(
    private val api: AudioImageApi,
    private var storageDir: String
) {

    suspend fun upload(filename: String, body: RequestBody): Result<Unit> {
        Log.d(TAG, "upload body. filename ${filename}")
        try {
            // check if img file already exists
            val imgFilename = "${filename.substringBeforeLast('.')}.png"
            val imgFile = File(storageDir, imgFilename)
            val exist = imgFile.exists()
            if (exist) {
                Log.d(TAG, "img file exists, not sending upload request")
                return Result.success(Unit)
            }

            // upload if img file not found
            val uploadResponse = api.uploadFile(body)
            Log.d(TAG, "response.code " + uploadResponse.code().toString())
            val msg = uploadResponse.body()?.msg ?: ""
            Log.d(TAG, msg)
            return when {
                uploadResponse.isSuccessful -> {
                    Result.success(Unit)
                }
                else -> Result.failure(Exception("upload error"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(Exception("upload error"))
        }
    }

    suspend fun downloadImage(filename: String): Result<String> {
        Log.d(TAG, "downloadImage")

        val imgFilename = "${filename.substringBeforeLast('.')}.png"
        val imgFile = File(storageDir, imgFilename)
        val exist = imgFile.exists()
        if (exist) {
            Log.d(TAG, "img file exists, not sending download request")
            return Result.success(imgFile.toString())
        }

        try {
            val response = api.getImage(filename)
            Log.d(TAG, "downloadImage response.code " + response.code().toString())

            return when {
                response.isSuccessful -> {
                    val body = response.body()
                    val imageFilename = imgFilename
                    val file = File(storageDir, imageFilename)
                    Log.d(TAG, "downloadImage to file ${file}")

                    if (body != null) {
                        Log.d(TAG, "downloadImage process body")
                        file.outputStream().use { outputStream ->
                            body.byteStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    }

                    return Result.success(file.toString())
                }
                else -> Result.failure(Exception("upload error"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(Exception("upload error"))
        }
    }

    companion object {
        const val TAG = "AudioImageRepo"
    }
}



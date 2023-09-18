package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.network.AudioImageRepo
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AudioImageViewModel(private val repo: AudioImageRepo) : ViewModel() {

    private val _statusString = MutableLiveData<String>()
    val statusString: LiveData<String> = _statusString

    private val _imagePath = MutableLiveData<String>()
    val imagePath: LiveData<String> = _imagePath

    fun upload(filepath: String) {
        viewModelScope.launch {
            _statusString.postValue("upload..")

            val file = File(filepath)
            Log.d(TAG, "upload file exist = ${file.exists()}")
            val requestBody =
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
                    )
                    .build()

            Log.d(TAG, "ViewModel upload ${filepath}")

            val res = repo.upload(file.name, requestBody)
            when (res.isSuccess) {
                true -> {
                    _statusString.postValue("upload success")
                    Log.d(TAG, "upload success")
                }
                else -> {
                    _statusString.postValue("upload failed")
                    Log.d(TAG, "upload failed")
                }
            }
        }
    }

    fun download(filename: String) {
        Log.d(TAG, "download ${filename}")
        viewModelScope.launch {
            _statusString.postValue("downloading..")
            val res = repo.downloadImage(filename)
            when (res.isSuccess) {
                true -> {
                    _statusString.postValue("download success")
                    val imgPath = res.getOrNull()
                    Log.d(TAG, "download success")
                    Log.d(TAG, imgPath ?: "")
                    if (imgPath != null && File(imgPath).exists()) {
                        _imagePath.postValue(imgPath!!)
                    }
//
                }
                else -> {
                    _statusString.postValue("download failed")
                    Log.d(TAG, "download failed")
                }
            }
        }
    }


    class Factory(
        private val repository: AudioImageRepo,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AudioImageViewModel(
                repo = repository
            ) as T
        }
    }

    companion object {
        const val TAG = "AudioImageViewModel"
    }
}

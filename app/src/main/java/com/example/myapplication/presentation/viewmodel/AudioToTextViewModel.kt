package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.network.AudioToTextRepo
import com.example.myapplication.network.GetResultResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AudioToTextViewModel(private val repo: AudioToTextRepo) : ViewModel() {
    private val _statusString = MutableLiveData<String>()
    val statusString: LiveData<String> = _statusString

    private val _textResult = MutableLiveData<String>()
    var textResult: LiveData<String> = _textResult

    private val _fileUrl = MutableLiveData<String>()
    val fileUrl: LiveData<String> = _fileUrl
//    private var fileUrl: String = ""

    fun uploadRequest(filepath: String) {
        viewModelScope.launch {
            _statusString.postValue("start upload request")
            val file = File(filepath)
            Log.d(TAG, "uploadRequest file exist = ${file.exists()}")

            val res = repo.uploadRequest(file.name)

            when (res.isSuccess) {
                true -> {
                    _statusString.postValue("upload request success")

                    val resultUrl = res.getOrNull()?.url ?: ""
                    val msg = res.getOrNull()?.msg ?: ""
                    Log.d(TAG, msg)
                    _fileUrl.postValue(resultUrl)

                    if (resultUrl == "") {
                        Log.d("**", "uploadRequest failed to set file url, but fileUrl is not set")
                        _statusString.postValue("upload request success. $msg")
                    } else {
                        Log.d(
                            TAG,
                            "uploadRequest set file url ${fileUrl.value?.substring(0, 10)}..."
                        )
                        _statusString.postValue("upload request success. $msg")
                    }
                }
                else -> {
                    val msg = res.exceptionOrNull()?.message
                    Log.d(TAG, "upload failed: $msg")
                    _statusString.postValue("upload request failed ($msg)")
                }
            }

            Log.d(TAG, "AudioToTextViewModel upload request $filepath")
        }
    }

    fun upload(filepath: String) {
        viewModelScope.launch {

            val file = File(filepath)
            Log.d(TAG, "upload file ${file.name}. exist = ${file.exists()}")

            val requestBody = File(filepath).asRequestBody("audio/mpeg".toMediaTypeOrNull())
            Log.d(TAG, "requestBody | ${requestBody.contentType()} }")

            if (fileUrl.value == null || fileUrl.value == "") {
                Log.e(TAG, "no fileUrl (${fileUrl})")
                _statusString.postValue("no fileUrl")
                return@launch
            }

            val res = repo.upload(fileUrl.value!!, requestBody)

            when (res.isSuccess) {
                true -> _statusString.postValue("upload success")
                else -> {
                    val msg = res.exceptionOrNull()?.message
                    Log.d(TAG, "upload failed: $msg")
                    _statusString.postValue("upload failed ($msg)")
                }
            }

            Log.d(TAG, "AudioToTextViewModel upload $filepath")
        }
    }

    fun getResult(filename: String) {
        Log.d(TAG, "getResult")
        viewModelScope.launch {
            val f = File(filename)
            val cleanFilename = f.nameWithoutExtension.replace(" ", "_")
            val res : Result<GetResultResponse?> = repo.getResult(cleanFilename)

            when (res.isSuccess) {
                true -> {
                    val str = res.getOrNull()?.text.toString()
                    _textResult.postValue(str)
                    val status = res.getOrNull()?.status.toString()
                    _statusString.postValue("get result success. status: $status")
                    Log.d(TAG, "getResult text: $str")
                }
                else -> {
                    val msg = res.exceptionOrNull()?.message
                    Log.d(TAG, "getResult failed: $msg")
                    _statusString.postValue("getResult failed ($msg)")
                }
            }

        }
    }

    class Factory(
        private val repository: AudioToTextRepo,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AudioToTextViewModel(
                repo = repository
            ) as T
        }
    }

    companion object {
        const val TAG = "AudioToTextViewModel"
    }
}

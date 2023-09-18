package com.example.myapplication.presentation.viewmodel

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.presentation.fragment.RecorderFragment
import java.io.IOException
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class RecorderViewModel(private var application: Application) : ViewModel() {
    private var recorder: MediaRecorder? = null
    private var filepath: String = ""
    private var storageDir: String = application.applicationContext.externalCacheDir.toString()
    private var isRecording: Boolean = false

    fun getFilepath(): String {
        return filepath
    }

    fun getFilename(): String {
        return filepath.substringAfterLast("/")
    }

    fun recording(): Boolean {
        return isRecording
    }

    fun setNewFilepath(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd__HH-mm-sss", Locale.ENGLISH)
        val currentDateTime: String = formatter.format(time)
        val ext = ".mp4"
        filepath = Paths.get(storageDir, "${currentDateTime}${ext}").toString()
        return filepath
    }

    fun startRecording() {
        setNewFilepath()
        Log.i(RecorderFragment.TAG, "startRecording  ${filepath}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(application.applicationContext)
        } else {
            recorder = MediaRecorder()
        }

        // recorder
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder!!.setOutputFile(filepath)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        try {
            recorder!!.prepare()
            recorder!!.start()
            isRecording = true
        } catch (e: IOException) {
            Log.e(TAG, "recorder failed", e)
        }
    }

    fun stopRecording() {
        Log.i(TAG, "stopRecording")
        recorder?.stop()
        isRecording = false
        recorder?.release()
        recorder = null
    }


    class Factory(private val application: () -> Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RecorderViewModel(application()) as T
        }
    }

    companion object {
        const val TAG = "RecorderViewModel"
    }
}
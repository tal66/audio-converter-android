package com.example.myapplication.presentation.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.FileModel
import java.io.IOException

class FileInfoViewModel : ViewModel() {
    private lateinit var fileInfo: FileModel
    private var player: MediaPlayer? = null
    var playStatus: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getFilename(): String {
        Log.i(TAG, "getFilename ${fileInfo.filename}")
        return this.fileInfo.filename
    }

    fun getFilepath(): String {
        Log.i(TAG, "getFilename ${fileInfo.filepath}")
        return this.fileInfo.filepath
    }

    fun seekTo(progressSeconds: Int) {
        if (player == null || playStatus.value == false) {
            return
        }
        Log.i(TAG, "seekTo ${progressSeconds}")
        player!!.seekTo(progressSeconds * 1000)
    }

    fun play() {
        Log.i(TAG, "playRecording")

        if (player != null) {
            stopPlaying()
        }
        player = MediaPlayer()

        player!!.setOnCompletionListener {
            stopPlaying()
            playStatus.value = false
        }

        try {
            player!!.setDataSource(fileInfo.filepath)
            player!!.prepare()
            player!!.start()
            playStatus.value = true
        } catch (e: IOException) {
            Log.e(TAG, "player failed", e)
            return
        }
    }

    fun stopPlaying() {
        Log.i(TAG, "stopPlaying")
        player?.release()
        player = null
        playStatus.value = false
    }

    fun setFile(fileModel: FileModel) {
        fileInfo = fileModel
    }

    fun getFile(): FileModel {
        return fileInfo
    }

    fun getCurrentPosition(): Int {
        if (player == null) {
            return 0
        }
        return player!!.currentPosition / 1000
    }

    companion object {
        const val TAG = "FileInfoViewModel"
    }
}
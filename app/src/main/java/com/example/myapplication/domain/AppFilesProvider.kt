package com.example.myapplication.domain

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class AppFilesProvider(
    val context: Context, val resolver: ContentResolver,
    val coroutineDispatcher: CoroutineDispatcher
) : FilesProvider {

    val dir = context.externalCacheDir

    override suspend fun getAllItems(): List<FileModel> {
        Log.d(TAG, "getAllItems. resolver=${resolver}")

        return withContext(coroutineDispatcher) {
            try {
                queryFiles()
            } catch (e: Exception) {
                Log.e(TAG, "Error queryFiles. dir ${dir}", e)
                emptyList()
            }
        }
    }

    private fun queryFiles(): List<FileModel> {
        val result = mutableListOf<FileModel>()
        val retriever = MediaMetadataRetriever()
        val files = dir?.listFiles()

        Log.d(TAG, "${files?.size} files found. dir ${dir}")

        files?.sortByDescending { it.lastModified() }
        files?.filter { it.path.endsWith("mp3") || it.path.endsWith("mp4") }?.forEach {
            val path = it.path

            retriever.setDataSource(path)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toInt()
            val durationSec = durationMs?.div(1000)
            result.add(FileModel(path, durationSec ?: 0, "<unkonwn>", "<unkonwn>"))
        }

        return result
    }

    companion object {
        const val TAG = "AppFilesProvider"
    }

}
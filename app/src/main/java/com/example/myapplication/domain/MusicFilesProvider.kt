package com.example.myapplication.domain

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class MusicFilesProvider(val context: Context, val resolver: ContentResolver, val coroutineDispatcher: CoroutineDispatcher) : FilesProvider{

    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override suspend fun getAllItems(): List<FileModel> {
        Log.d(TAG, "getAllItems. resolver=${resolver}")

        return withContext(coroutineDispatcher) {
            queryFiles()
        }
    }

    private fun queryFiles(): List<FileModel> {
        Log.d(TAG, "queryFiles. uri ${uri}")
        val result = mutableListOf<FileModel>()

        return try {
            resolver.query(uri!!, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

                    do {
                        val currTitle = cursor.getString(titleColumn)
                        val currArtist = cursor.getString(artistColumn)
                        val currDuration = cursor.getInt(durationColumn) / 1000
                        val currPath = cursor.getString(pathColumn)

                        result.add(FileModel(currPath, currDuration, currArtist, currTitle))

                        Log.i(TAG, " ${currPath}")
                    } while (cursor.moveToNext())
                }
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error querying media store", e)
            result
        }
    }


    companion object {
        const val TAG = "MusicFilesProvider"
    }

}
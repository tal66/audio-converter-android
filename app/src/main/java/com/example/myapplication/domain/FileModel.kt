package com.example.myapplication.domain
import java.io.Serializable



data class FileModel(val filepath: String, val durationSec: Int = 0, val artist: String = "", val title: String = "") : Serializable
{
    val filename: String = filepath.substringAfterLast("/", filepath)
    val durationDisplay : String = String.format("%02d:%02d", durationSec / 60, durationSec % 60)
}
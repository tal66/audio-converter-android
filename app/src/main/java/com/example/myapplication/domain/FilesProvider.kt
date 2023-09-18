package com.example.myapplication.domain

interface FilesProvider {
    suspend fun getAllItems(): List<FileModel>
}
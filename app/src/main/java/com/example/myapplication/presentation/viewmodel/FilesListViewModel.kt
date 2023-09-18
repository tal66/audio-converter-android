package com.example.myapplication.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.myapplication.domain.FileModel
import com.example.myapplication.domain.FilesProvider
import kotlinx.coroutines.launch

class FilesListViewModel(
    private var itemsProvider: FilesProvider
) : ViewModel() {

    private var selectedFile = 0
    private val files: MutableLiveData<List<FileModel>> = MutableLiveData()

    init {
        refreshItems()
    }

    fun getData(): MutableLiveData<List<FileModel>> {
        return files
    }

    fun getSelectedFile(): FileModel? {
        Log.d(TAG, "selectedFile is: ${selectedFile}")
        return files.value?.get(selectedFile)
    }

    fun updateSelectedFile(selected: Int) {
        selectedFile = selected
    }

    fun changeProvider(itemsProvider: FilesProvider) {
        if (this.itemsProvider == itemsProvider) {
            Log.d(TAG, "changeProvider: provider already selected")
            return
        }
        this.itemsProvider = itemsProvider
        refreshItems()
    }

    fun refreshItems() {
        Log.d(TAG, "refreshItems")
        viewModelScope.launch {
            val items = itemsProvider.getAllItems()
            files.postValue(items)
        }
    }


    class Factory(private val itemsProviderFactory: () -> FilesProvider) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return FilesListViewModel(itemsProviderFactory()) as T
        }
    }

    companion object {
        const val TAG = "FilesListViewModel"
    }
}
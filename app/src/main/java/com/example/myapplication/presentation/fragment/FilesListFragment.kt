package com.example.myapplication.presentation.fragment

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.get
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.presentation.adapter.FileAdapter
import com.example.myapplication.R
import com.example.myapplication.domain.AppFilesProvider
import com.example.myapplication.domain.FilesProvider
import com.example.myapplication.domain.MusicFilesProvider
import com.example.myapplication.presentation.viewmodel.FilesListViewModel
import kotlinx.coroutines.Dispatchers

class FilesListFragment : Fragment() {

    private lateinit var viewModel: FilesListViewModel
    private lateinit var recyclerView: RecyclerView

    // btns
    private lateinit var refreshBtn: Button
    private lateinit var showMusicFilesBtn: Button
    private lateinit var showRecordingsBtn: Button
    private val notSelectedColor = Color.parseColor("#FF6200EE")
    private val selectedColor = Color.BLACK

    // providers
    private lateinit var appFilesProvider: FilesProvider
    private lateinit var musicFilesProvider: FilesProvider

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Files List"

        val view = inflater.inflate(R.layout.fragment_files_list, container, false)
        refreshBtn = view.findViewById(R.id.refresh_btn)
        showMusicFilesBtn = view.findViewById(R.id.show_files_btn)
        showRecordingsBtn = view.findViewById(R.id.show_rec_files_btn)

        appFilesProvider = AppFilesProvider(
            context = requireContext(),
            resolver = requireActivity().contentResolver,
            coroutineDispatcher = Dispatchers.IO
        )

        musicFilesProvider = MusicFilesProvider(
            context = requireContext(),
            resolver = requireActivity().contentResolver,
            coroutineDispatcher = Dispatchers.IO
        )

        viewModel = ViewModelProvider(this, FilesListViewModel.Factory(
            itemsProviderFactory = {
                musicFilesProvider
            }
        )).get()

        // btns
        refreshBtn.setOnClickListener {
            viewModel.refreshItems()
            recyclerView.scrollToPosition(0)
        }

        showMusicFilesBtn.setBackgroundColor(selectedColor)

        showMusicFilesBtn.setOnClickListener {
            viewModel.changeProvider(musicFilesProvider)
            it.setBackgroundColor(selectedColor)
            showRecordingsBtn.setBackgroundColor(notSelectedColor)
        }

        showRecordingsBtn.setOnClickListener {
            viewModel.changeProvider(appFilesProvider)
            it.setBackgroundColor(selectedColor)
            showMusicFilesBtn.setBackgroundColor(notSelectedColor)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        // recyclerView
        recyclerView = view.findViewById(R.id.files_list)

        val fileAdapter = FileAdapter(viewModel)
        viewModel.getData().observe(viewLifecycleOwner) {
            Log.d(TAG, "observer new value. size ${it.size}")
            it?.let { fileAdapter.submitList(it) }
        }

        recyclerView.adapter = fileAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        (recyclerView.adapter as FileAdapter).onClick = {
            val action = FilesListFragmentDirections.actionFilesListFragmentToFileInfoFragment(
                viewModel.getSelectedFile()
            )
            view.findNavController().navigate(action)

        }
    }


    companion object {
        const val TAG = "FilesListFragment"
    }
}
package com.example.myapplication.presentation.fragment


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import androidx.lifecycle.get
import com.example.myapplication.R
import com.example.myapplication.presentation.viewmodel.RecorderViewModel


class RecorderFragment : Fragment() {

    private lateinit var startRecordingBtn: Button
    private lateinit var stopRecordingBtn: Button

    private lateinit var infoTextView: TextView
    private lateinit var chronometer: Chronometer
    private lateinit var viewModel: RecorderViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recorder, container, false)
        activity?.title = getString(R.string.recorder)

        viewModel = ViewModelProvider(this, RecorderViewModel.Factory(
            application = {
                requireActivity().application
            }
        )).get()

        // views
        startRecordingBtn = view.findViewById(R.id.start_record)
        stopRecordingBtn = view.findViewById(R.id.stop_rec)
        infoTextView = view.findViewById(R.id.infoTextView)
        chronometer = view.findViewById(R.id.chronometer)

        startRecordingBtn.setOnClickListener {
            startRecording()
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }
        stopRecordingBtn.setOnClickListener {
            chronometer.stop()
            stopRecording()
        }

        return view
    }

    private fun startRecording() {
        viewModel.startRecording()
        val filename = viewModel.getFilename()
        Log.i(TAG, "startRecording  $filename")
        infoTextView.text = getString(R.string.start_recording_text, filename)
    }

    private fun stopRecording() {
        if (!viewModel.recording()) {
            return
        }
        viewModel.stopRecording()
        Log.i(TAG, "stopRecording")
        infoTextView.text = getString(R.string.saved_recording_text, viewModel.getFilename())
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    companion object {
        const val TAG = "RecorderFragment"
    }
}
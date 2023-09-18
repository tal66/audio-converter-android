package com.example.myapplication.presentation.fragment

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import com.example.myapplication.R
import com.example.myapplication.network.*
import com.example.myapplication.presentation.viewmodel.AudioImageViewModel
import com.example.myapplication.presentation.viewmodel.AudioToTextViewModel
import com.example.myapplication.presentation.viewmodel.FileInfoViewModel
import java.io.IOException

class FileInfoFragment : Fragment() {

    private lateinit var viewModel: FileInfoViewModel
    private lateinit var msgTextView: TextView
    private lateinit var infoTextView: TextView

    // audio player
    private lateinit var playBtn: ImageButton
    private lateinit var stopPlayingBtn: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var playerStatusTextView: TextView

    // api
    private lateinit var audioImageviewModel: AudioImageViewModel
    private lateinit var audioToTextviewModel: AudioToTextViewModel

    // txt api views
    private lateinit var txtUploadReqBtn: Button
    private lateinit var txtResultsBtn: Button
    private lateinit var textResult: TextView
    private lateinit var textResultHeadline: TextView
    private lateinit var txtApiStatusText: TextView

    // img api
    private lateinit var imgUploadBtn: Button
    private lateinit var imgResultsBtn: Button
    private lateinit var imgApiStatusText: TextView
    private lateinit var audioImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "File Info"
        val view = inflater.inflate(R.layout.fragment_file_info, container, false)

        // views
        setupViews(view)

        // main viewModel, safe args
        viewModel = ViewModelProvider(this).get(FileInfoViewModel::class.java)
        val msg = FileInfoFragmentArgs.fromBundle(requireArguments()).filedata
        if (msg != null) {
            viewModel.setFile(msg)
            infoTextView.text = getString(R.string.file_info_text, msg.filename, msg.durationDisplay)
        }

        // audio api viewModels
        setupAudioApiViewModels()

        // audio api listeners
        setupAudioToTextApiListeners()

        // player listeners
        setupPlayerListeners()

        return view
    }

    private fun setupAudioApiViewModels() {
        audioImageviewModel = ViewModelProvider(
            this,
            AudioImageViewModel.Factory(
                repository = AudioImageRepo(
                    AudioImageApi.instance, requireContext().externalCacheDir.toString()
                )
            )
        ).get(AudioImageViewModel::class.java)

        audioToTextviewModel = ViewModelProvider(
            this,
            AudioToTextViewModel.Factory(
                repository = AudioToTextRepo(AudioToTextApi.instance)
            )
        ).get(AudioToTextViewModel::class.java)
    }

    private fun setupViews(view: View) {
        infoTextView = view.findViewById(R.id.fileDetails_text_view)
        playerStatusTextView = view.findViewById(R.id.playStatusTextView)
        playBtn = view.findViewById(R.id.player_play)
        stopPlayingBtn = view.findViewById(R.id.player_stop)
        seekBar = view.findViewById(R.id.seekBar)

        // txt api btns
        txtUploadReqBtn = view.findViewById(R.id.info_upload_req_txtApi_btn)
        txtResultsBtn = view.findViewById(R.id.info_txt_results_btn)
        textResult = view.findViewById(R.id.info_textResult_TextView)
        textResult.text = ""
        textResultHeadline = view.findViewById(R.id.info_textRes_headline_textView)
        txtApiStatusText = view.findViewById(R.id.info_txt_status_TextView)
        // img api btns
        imgUploadBtn = view.findViewById(R.id.info_upload_imgApi_btn)
        imgResultsBtn = view.findViewById(R.id.info_img_results_btn)
        audioImage = view.findViewById(R.id.info_audio_imageView)
        imgApiStatusText = view.findViewById(R.id.info_img_status_TextView)
    }

    private fun setupAudioToTextApiListeners() {
        audioToTextviewModel.statusString.observe(viewLifecycleOwner) {
            txtApiStatusText.text = it
        }

        audioToTextviewModel.fileUrl.observe(viewLifecycleOwner) {
            if (it != "" && URLUtil.isValidUrl(it)) {
                Log.d(TAG, "initiating upload")
                txtApiStatusText.text = getString(R.string.upload)
                audioToTextviewModel.upload(viewModel.getFilepath())
            }
        }

        audioToTextviewModel.textResult.observe(viewLifecycleOwner) {
            textResult.text = it
            if (it != "") {
                textResultHeadline.text = getString(R.string.text_from_record_headline)
            }
        }

        txtUploadReqBtn.setOnClickListener {
            audioToTextviewModel.uploadRequest(viewModel.getFilepath())
        }

        txtResultsBtn.setOnClickListener {
            audioToTextviewModel.getResult(viewModel.getFilename())
        }

        // img api

        imgUploadBtn.setOnClickListener {
            audioImageviewModel.upload(viewModel.getFilepath())
        }
        imgResultsBtn.setOnClickListener {
            audioImageviewModel.download(viewModel.getFilename())
        }

        audioImageviewModel.statusString.observe(viewLifecycleOwner) {
            imgApiStatusText.text = it
        }
        audioImageviewModel.imagePath.observe(viewLifecycleOwner) {
            audioImage.setImageURI(Uri.parse(it))
        }
    }

    private fun setupPlayerListeners() {
        // seekbar progress
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentPosition = viewModel.getCurrentPosition()
                seekBar.progress = currentPosition
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 0)

        // listeners
        playBtn.setOnClickListener {
            play()
        }
        stopPlayingBtn.setOnClickListener {
            stopPlaying()
        }

        viewModel.playStatus.observe(viewLifecycleOwner) {
            when (it) {
                true -> playerStatusTextView.text = getString(R.string.status_playing)
                else -> playerStatusTextView.text = getString(R.string.status_stopped)
            }
        }
    }


    private fun play() {
        Log.i(TAG, "play ${viewModel.getFilename()}")
        seekBar.max = viewModel.getFile().durationSec
        seekBar.progress = 0

        try {
            viewModel.play()

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        viewModel.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seek: SeekBar) {
                    // progress started
                }

                override fun onStopTrackingTouch(seek: SeekBar) {
                    // progress stopped
                    viewModel.seekTo(seek.progress)
                }
            })
        } catch (e: IOException) {
            Log.e(TAG, "player failed", e)
            msgTextView.text = getString(R.string.start_recording_text, viewModel.getFilename())
            return
        }
    }

    private fun stopPlaying() {
        Log.i(TAG, "stopPlaying")
        viewModel.stopPlaying()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        stopPlaying()
    }

    companion object {
        const val TAG = "FileInfoFragment"
    }
}
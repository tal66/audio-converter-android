package com.example.myapplication.presentation.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.domain.FileModel
import com.example.myapplication.R
import com.example.myapplication.presentation.viewmodel.FilesListViewModel

class FileAdapter(private val viewModel: FilesListViewModel) : ListAdapter<FileModel, FileAdapter.FileViewHolder>(
    DiffCallback()
) {

    var onClick: ((Int) -> Unit)? = null
    var prevItemView : View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val v: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return FileViewHolder(v)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            viewModel.updateSelectedFile(position)
            onClick?.invoke(position)

            holder.itemView.setBackgroundColor(Color.LTGRAY)
            prevItemView?.setBackgroundColor(Color.TRANSPARENT)
            prevItemView = holder.itemView
        }
    }


    //// ViewHolder

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView = itemView.findViewById<TextView>(R.id.list_item_file)
        val descView = itemView.findViewById<TextView>(R.id.list_item_description)
        val durationView = itemView.findViewById<TextView>(R.id.list_item_duration)

        // add data to item's views
        fun bind(item: FileModel?) {
            titleView.text = item?.filename
            durationView.text = item?.durationDisplay
            descView.text = "${item?.title} by ${ item?.artist }"
        }
    }

    //// Diff

    class DiffCallback : DiffUtil.ItemCallback<FileModel>() {
        override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            Log.d(TAG,"DiffUtil ${oldItem == newItem}" )
            return oldItem.filepath == newItem.filepath
        }

        override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
            Log.d(TAG,"DiffUtil ${oldItem == newItem}" )
            return oldItem == newItem
        }
    }


    companion object {
        const val TAG = "FileAdapter"
    }
}
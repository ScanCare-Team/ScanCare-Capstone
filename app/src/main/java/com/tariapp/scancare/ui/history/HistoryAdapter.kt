package com.tariapp.scancare.ui.history

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tariapp.scancare.data.ScancareEntity
import com.tariapp.scancare.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val deleteClick: (ScancareEntity) -> Unit,
    private val onItemClicked: (ScancareEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private val history = mutableListOf<ScancareEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(historyList: List<ScancareEntity>){
        history.clear()
        history.addAll(historyList)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(historyItem: ScancareEntity){
            // Menampilkan label klasifikasi
            binding.tvNamaHistory.text= historyItem.scanName
            binding.tvStatus.text = historyItem.status

            val imageUri = Uri.parse(historyItem.imageUri)
            Log.d("HistoryAdapter", "Binding image URI: $imageUri")
            try {
                Glide.with(binding.imgItemHistory.context)
                    .load(imageUri)
                    .into(binding.imgItemHistory)
            } catch (e: Exception) {
                Log.e("HistoryAdapter", "Error loading image: ${historyItem.imageUri}", e)
            }
            itemView.setOnClickListener {
                onItemClicked(historyItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = history.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(history[position])

        val delete = holder.binding.delete
        delete.setOnClickListener {
            deleteClick(history[position])
        }
    }

}
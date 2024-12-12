package com.fruitastic.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fruitastic.R
import com.fruitastic.data.local.entity.HistoryEntity
import com.fruitastic.databinding.ItemHistoryBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HistoryAdapter : ListAdapter<HistoryEntity, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(history: HistoryEntity) {
            val result = "${history.result} ${history.score}%"
            val color = when {
                history.result.contains("Fresh", true) -> ContextCompat.getColor(binding.root.context, R.color.green)
                history.result.contains("Mild", true) -> ContextCompat.getColor(binding.root.context, R.color.orange)
                history.result.contains("Rotten", true) -> ContextCompat.getColor(binding.root.context, R.color.red)
                else -> ContextCompat.getColor(binding.root.context, R.color.grey)
            }

            val drawable = binding.tvResult.background as GradientDrawable
            drawable.setColor(color)

            with(binding) {
                tvResult.text = result
                Glide.with(ivImage.context)
                    .load(Uri.parse(history.image))
                    .into(ivImage)
                tvTime.text = formatTimestamp(history.time)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val now = LocalDateTime.now()
            val dateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss")

            return when {
                dateTime.toLocalDate() == now.toLocalDate() -> {
                    "${binding.root.context.getString(R.string.today)}, ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}"
                }
                dateTime.toLocalDate() == now.toLocalDate().minusDays(1) -> {
                    "${binding.root.context.getString(R.string.yesterday)}, ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}"
                }
                else -> {
                    dateTime.format(formatter)
                }
            }
        }
    }



    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HistoryEntity> =
            object : DiffUtil.ItemCallback<HistoryEntity>() {
                override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

package com.origin.commons.callerid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.origin.commons.callerid.databinding.CellReminderItemsBinding
import com.origin.commons.callerid.db.entity.ReminderEntity

class ReminderAdapter(private val onItemClick: (reminder: ReminderEntity) -> Unit, private val onDeleteClick: (reminder: ReminderEntity) -> Unit) :
    ListAdapter<ReminderEntity, ReminderAdapter.ReminderViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(CellReminderItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val items = getItem(position)
        if (items != null) {
            holder.bind(items)
        }
    }

    inner class ReminderViewHolder(private val _binding: CellReminderItemsBinding) : ViewHolder(_binding.root) {
        fun bind(data: ReminderEntity) {
            with(_binding) {
                tvReminderText.text = data.title
                tvReminderDate.text = data.date
                val fullTime = data.hours + ":" + data.minutes
                tvReminderTime.text = fullTime

                root.setOnClickListener {
                    onItemClick.invoke(data)
                }
                ivDelete.setOnClickListener {
                    onDeleteClick.invoke(data)
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ReminderEntity>() {
            override fun areItemsTheSame(oldItem: ReminderEntity, newItem: ReminderEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: ReminderEntity, newItem: ReminderEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}
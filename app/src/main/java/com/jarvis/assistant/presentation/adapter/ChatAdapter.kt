package com.jarvis.assistant.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jarvis.assistant.R
import com.jarvis.assistant.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private val onItemClick: (Message) -> Unit = {}
) : ListAdapter<Message, ChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutUser = itemView.findViewById<View>(R.id.layoutUser)
        private val layoutAssistant = itemView.findViewById<View>(R.id.layoutAssistant)
        private val tvUserMessage = itemView.findViewById<TextView>(R.id.tvUserMessage)
        private val tvAssistantMessage = itemView.findViewById<TextView>(R.id.tvAssistantMessage)
        private val tvUserTimestamp = itemView.findViewById<TextView>(R.id.tvUserTimestamp)
        private val tvAssistantTimestamp = itemView.findViewById<TextView>(R.id.tvAssistantTimestamp)

        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(message: Message) {
            val isUser = message.role.lowercase() == "user"
            layoutUser.visibility = if (isUser) View.VISIBLE else View.GONE
            layoutAssistant.visibility = if (isUser) View.GONE else View.VISIBLE

            if (isUser) {
                tvUserMessage.text = message.content
                tvUserTimestamp.text = dateFormat.format(Date(message.timestamp))
            } else {
                tvAssistantMessage.text = if (message.content.isEmpty() && message.isStreaming) {
                    "▊" // Blinking cursor for streaming
                } else {
                    message.content + if (message.isStreaming) " ▊" else ""
                }
                tvAssistantTimestamp.text = dateFormat.format(Date(message.timestamp))
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem.id == newItem.id && oldItem.role == newItem.role

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
            oldItem == newItem
    }
}

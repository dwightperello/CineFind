package com.example.cinefind.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cinefind.R
import com.example.cinefind.data.model.Genre

class GenreAdapter(
    private val onGenreClick: (Genre) -> Unit
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    private val genres = mutableListOf<Genre>()
    private var selectedPosition = RecyclerView.NO_ID.toInt()

    fun submitList(list: List<Genre>) {
        genres.clear()
        genres.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genre, parent, false) as TextView
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(genres[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            val newPosition = holder.bindingAdapterPosition
            if (newPosition == RecyclerView.NO_POSITION) return@setOnClickListener
            val previous = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
            onGenreClick(genres[selectedPosition])
        }
    }

    override fun getItemCount() = genres.size

    class GenreViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(genre: Genre, isSelected: Boolean) {
            textView.text = genre.name
            textView.isSelected = isSelected
            textView.setTextColor(
                ContextCompat.getColor(
                    textView.context,
                    if (isSelected) R.color.white else R.color.purple_500
                )
            )
        }
    }
}

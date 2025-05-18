package com.example.match3game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class TileAdapter(
    private val tiles: List<Tile>,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<TileAdapter.TileViewHolder>() {

    inner class TileViewHolder(val tileText: TextView) : RecyclerView.ViewHolder(tileText) {
        init {
            tileText.setOnClickListener {
                clickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tile_item, parent, false) as TextView
        return TileViewHolder(textView)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        val tile = tiles[position]
        holder.tileText.text = tile.emoji
        holder.tileText.animate().alpha(if (tile.isMatched) 0f else 1f).duration = 300
    }

    override fun getItemCount() = tiles.size
}


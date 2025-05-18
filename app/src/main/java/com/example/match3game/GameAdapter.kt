package com.example.match3game

import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class GameAdapter(
    private val context: Context,
    private val tiles: MutableList<Tile>,
    private val boardWidth: Int,
    private val onSwap: (from: Int, to: Int) -> Unit
) : RecyclerView.Adapter<GameAdapter.TileViewHolder>() {

    inner class TileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnTouchListener {

        private var downX = 0f
        private var downY = 0f

        init {
            itemView.setOnTouchListener(this)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event == null) return false

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    val upX = event.x
                    val upY = event.y

                    val deltaX = upX - downX
                    val deltaY = upY - downY

                    val position = adapterPosition
                    val swipeThreshold = 30

                    val newPosition = when {
                        abs(deltaX) > abs(deltaY) && abs(deltaX) > swipeThreshold -> {
                            if (deltaX > 0) position + 1 else position - 1
                        }

                        abs(deltaY) > swipeThreshold -> {
                            if (deltaY > 0) position + boardWidth else position - boardWidth
                        }

                        else -> -1
                    }

                    if (newPosition in tiles.indices && isAdjacent(position, newPosition)) {
                        onSwap(position, newPosition)
                    }

                    return true
                }
            }
            return false
        }

        fun bind(tile: Tile) {
            val tileView = itemView as TextView
            tileView.text = tile.emoji
        }
    }

    private fun isAdjacent(pos1: Int, pos2: Int): Boolean {
        val row1 = pos1 / boardWidth
        val col1 = pos1 % boardWidth
        val row2 = pos2 / boardWidth
        val col2 = pos2 % boardWidth
        return (abs(row1 - row2) == 1 && col1 == col2) || (abs(col1 - col2) == 1 && row1 == row2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tile_item, parent, false)
        return TileViewHolder(view)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        holder.bind(tiles[position])
    }

    override fun getItemCount(): Int = tiles.size
}

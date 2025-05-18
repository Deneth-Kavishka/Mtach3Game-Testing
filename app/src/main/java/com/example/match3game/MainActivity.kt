package com.example.match3game

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var boardRecyclerView: RecyclerView
    private lateinit var adapter: TileAdapter
    private val boardSize = 8
    private val emojis = listOf("🍎", "🍊", "🍇", "🍓", "🍒", "🥝")
    private var board = mutableListOf<Tile>()
    private var selectedPosition: Int? = null

    private var score = 0
    private lateinit var scoreTextView: TextView

    private var movesLeft = 20
    private lateinit var movesTextView: TextView

    private lateinit var matchSound: MediaPlayer
    private lateinit var gameOverSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardRecyclerView = findViewById(R.id.boardRecyclerView)
        boardRecyclerView.layoutManager = GridLayoutManager(this, boardSize)

        scoreTextView = findViewById(R.id.scoreText)
        movesTextView = findViewById(R.id.movesText)

        matchSound = MediaPlayer.create(this, R.raw.match_sound)
        gameOverSound = MediaPlayer.create(this, R.raw.game_over)

        initBoard()
        adapter = TileAdapter(board) { position -> onTileClicked(position) }
        boardRecyclerView.adapter = adapter

        updateScore(0)
        updateMoves(0)
    }

    private fun initBoard() {
        board.clear()
        for (i in 0 until boardSize * boardSize) {
            board.add(Tile(emojis.random()))
        }
        checkMatches()
    }

    private fun onTileClicked(position: Int) {
        val previous = selectedPosition
        selectedPosition = position
        if (previous != null && previous != position) {
            if (areAdjacent(previous, position)) {
                swapTiles(previous, position)
                updateMoves(-1)
                checkMatches()
                selectedPosition = null
                adapter.notifyDataSetChanged()
            } else {
                selectedPosition = position
            }
        }
    }

    private fun areAdjacent(pos1: Int, pos2: Int): Boolean {
        val row1 = pos1 / boardSize
        val col1 = pos1 % boardSize
        val row2 = pos2 / boardSize
        val col2 = pos2 % boardSize
        return (row1 == row2 && kotlin.math.abs(col1 - col2) == 1) ||
                (col1 == col2 && kotlin.math.abs(row1 - row2) == 1)
    }

    private fun swapTiles(pos1: Int, pos2: Int) {
        val temp = board[pos1].emoji
        board[pos1].emoji = board[pos2].emoji
        board[pos2].emoji = temp
    }

    private fun checkMatches() {
        var matched = false
        for (i in board.indices) {
            board[i].isMatched = false
        }

        // Check rows
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize - 2) {
                val index = row * boardSize + col
                val t1 = board[index]
                val t2 = board[index + 1]
                val t3 = board[index + 2]
                if (t1.emoji == t2.emoji && t2.emoji == t3.emoji) {
                    t1.isMatched = true
                    t2.isMatched = true
                    t3.isMatched = true
                    matched = true
                    updateScore(10)
                    matchSound.start()
                }
            }
        }

        // Check columns
        for (col in 0 until boardSize) {
            for (row in 0 until boardSize - 2) {
                val index = row * boardSize + col
                val t1 = board[index]
                val t2 = board[index + boardSize]
                val t3 = board[index + 2 * boardSize]
                if (t1.emoji == t2.emoji && t2.emoji == t3.emoji) {
                    t1.isMatched = true
                    t2.isMatched = true
                    t3.isMatched = true
                    matched = true
                    updateScore(10)
                    matchSound.start()
                }
            }
        }

        if (matched) {
            removeMatchedTiles()
        }
    }

    private fun removeMatchedTiles() {
        for (i in board.indices) {
            if (board[i].isMatched) {
                board[i] = Tile(emojis.random())
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateScore(points: Int) {
        score += points
        scoreTextView.text = "Score: $score"
    }

    private fun updateMoves(change: Int) {
        movesLeft += change
        movesTextView.text = "Moves: $movesLeft"
        if (movesLeft <= 0) {
            showGameOver()
        }
    }

    private fun showGameOver() {
        gameOverSound.start()
        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Your Score: $score")
            .setPositiveButton("Restart") { _, _ ->
                restartGame()
            }
            .setCancelable(false)
            .show()
    }

    private fun restartGame() {
        score = 0
        movesLeft = 20
        initBoard()
        adapter.notifyDataSetChanged()
        updateScore(0)
        updateMoves(0)
    }
}

package com.mertatmaca.worldeapp

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mertatmaca.worldeapp.databinding.WordleBinding

class wordle : AppCompatActivity() {

    private lateinit var binding: WordleBinding
    private var correctWord = ""
    private var currentRow = 0
    private val maxAttempts = 5
    private val boxRefs = mutableListOf<MutableList<TextView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WordleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startNewGame()

        binding.submitGuess.setOnClickListener {
            val input = binding.guessInput.text.toString().lowercase()
            if (input.length != 5) {
                Toast.makeText(this, "5 harfli bir kelime girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentRow >= maxAttempts) return@setOnClickListener

            for (i in input.indices) {
                val box = boxRefs[currentRow][i]
                box.text = input[i].uppercase()
                box.animate()
                    .setStartDelay(i * 200L) // her kutu 200ms gecikmeli başlar
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .rotationX(360f)
                    .setDuration(300)
                    .withEndAction {
                        box.setBackgroundColor(Color.parseColor(getColorForLetter(input[i], i)))
                        box.setTextColor(Color.WHITE)
                    }
                    .start()


                //box.setTextColor(Color.WHITE)
                //box.setBackgroundColor(Color.parseColor(getColorForLetter(input[i], i)))
            }

            currentRow++
            binding.guessInput.text.clear()

            if (input == correctWord) {
                Toast.makeText(this, "Tebrikler! Kelime doğru.", Toast.LENGTH_LONG).show()
                binding.submitGuess.isEnabled = false
                showRestartPrompt()
            } else if (currentRow == maxAttempts) {
                Toast.makeText(this, "Bitti! Kelime: $correctWord", Toast.LENGTH_LONG).show()
                binding.submitGuess.isEnabled = false
                showRestartPrompt()
            }
        }
    }

    private fun startNewGame() {
        binding.gridLayout.removeAllViews()
        boxRefs.clear()
        currentRow = 0
        correctWord = WordList.words.random()
        setupEmptyGrid()
        binding.submitGuess.isEnabled = true
    }

    private fun showRestartPrompt() {
        AlertDialog.Builder(this)
            .setTitle("Oyun Bitti")
            .setMessage("Tekrar oynamak ister misin?")
            .setPositiveButton("Evet") { _, _ -> startNewGame() }
            .setNegativeButton("Hayır") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun setupEmptyGrid() {
        for (row in 0 until maxAttempts) {
            val rowBoxes = mutableListOf<TextView>()
            for (col in 0 until 5) {
                val box = TextView(this).apply {
                    text = ""
                    gravity = Gravity.CENTER
                    textSize = 38f
                    setTextColor(Color.BLACK)
                    setBackgroundColor(Color.WHITE)
                    setPadding(2, 2, 2, 2)
                    layoutParams = android.widget.GridLayout.LayoutParams().apply {
                        width = dpToPx(68)
                        height = dpToPx(68)
                        rowSpec = android.widget.GridLayout.spec(row)
                        columnSpec = android.widget.GridLayout.spec(col)
                        setMargins(8, 8, 8, 8)
                    }
                    setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
                }
                binding.gridLayout.addView(box)
                rowBoxes.add(box)
            }
            boxRefs.add(rowBoxes)
        }
    }

    private fun getColorForLetter(letter: Char, index: Int): String {
        return when {
            letter == correctWord[index] -> "#00AA00" // green
            letter in correctWord -> "#CCCC00" // yellow
            else -> "#808080" // black
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}

package com.example.wordle

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordle.databinding.ActivityQuizBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val wordList = WordList.words
    private val progressMap: MutableMap<String, WordProgress> = mutableMapOf()

    // Kelime tekrar aralıkları (milisaniye cinsinden)
    private val reviewIntervals = listOf(
        0L, // Hemen tekrar
        1 * 24 * 60 * 60 * 1000L, // 1 gün sonra
        7 * 24 * 60 * 60 * 1000L, // 7 gün sonra
        30 * 24 * 60 * 60 * 1000L, // 30 gün sonra
        90 * 24 * 60 * 60 * 1000L, // 90 gün sonra
        180 * 24 * 60 * 60 * 1000L, // 180 gün sonra
        365 * 24 * 60 * 60 * 1000L // 365 gün sonra
    )

    private var quizWords = listOf<Word>()
    private var currentIndex = 0
    private lateinit var currentWord: Word
    private lateinit var correctAnswer: String
    private var maxDailyWords = 10 // Günlük maksimum kelime sayısı

    // Yeni eklenen değişkenler: Doğru ve yanlış cevap sayıları
    private var correctAnswersCount = 0
    private var incorrectAnswersCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kullanıcının "kelimeSayisi" değerini Firestore'dan çek
        currentUserId?.let { uid ->
            firestore.collection("Users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val count = documentSnapshot.getLong("kelimeSayisi")?.toInt() ?: 10
                    maxDailyWords = count
                    loadProgressFromFirestore() // Kelime ilerlemesini yükle
                }
                .addOnFailureListener {
                    maxDailyWords = 10 // Hata olursa varsayılan değer
                    loadProgressFromFirestore()
                }
        } ?: run {
            maxDailyWords = 10 // Kullanıcı yoksa varsayılan değer
            loadProgressFromFirestore()
        }

        // Çıkış butonu - AnalystActivity'ye doğru ve yanlış sayısını göndererek geçiş yap
        binding.outImage.setOnClickListener {
            val intent = Intent(this, AnalystActivity::class.java)
            // Doğru ve yanlış cevap sayılarını Intent'e ekle
            intent.putExtra("CORRECT_ANSWERS", correctAnswersCount)
            intent.putExtra("INCORRECT_ANSWERS", incorrectAnswersCount)
            startActivity(intent)
            finish() // QuizActivity'yi kapat
        }

        // Sonraki kelime butonu
        binding.nextImage.setOnClickListener {
            currentIndex++
            if (currentIndex < quizWords.size) {
                showNextQuestion() // Bir sonraki soruyu göster
            } else {
                // Quiz bittiğinde yeni kelime seti oluştur ve baştan başla
                quizWords = getTodaysQuizWords().take(maxDailyWords)
                currentIndex = 0
                if (quizWords.isEmpty()) {
                    Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
                    finish() // Kelime yoksa aktiviteyi bitir
                } else {
                    showNextQuestion()
                }
            }
        }

        // Seçenek butonlarına tıklama dinleyicileri
        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
        )

        optionButtons.forEach { button ->
            button.setOnClickListener {
                checkAnswer(button.text.toString()) // Cevabı kontrol et
            }
        }
    }

    // Quiz'i günlük kelime limitiyle başlat
    private fun startQuizWithLimit() {
        quizWords = getTodaysQuizWords().take(maxDailyWords)
        if (quizWords.isEmpty()) {
            Toast.makeText(this, "Bugün için tekrar edilecek kelime yok!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            showNextQuestion()
        }
    }

    // Kelime ilerlemesini başlangıçta ilklendir
    private fun initializeProgress() {
        wordList.forEach { word ->
            // Her kelime için progressMap'te bir giriş olduğundan emin ol
            if (!progressMap.containsKey(word.createdBy)) {
                progressMap[word.createdBy] = WordProgress(word.createdBy, 0, 0L, mutableListOf())
            }
        }
    }

    // Kelimenin tekrar için uygun olup olmadığını kontrol et
    private fun isEligibleForReview(progress: WordProgress): Boolean {
        // Geçmiş 6'dan büyükse artık tekrar etme (ilerleme tamamlanmış)
        if (progress.history.size >= 6) return false
        val now = System.currentTimeMillis()
        // Gerekli tekrar gecikmesini al, yoksa maksimum değeri kullan
        val requiredDelay = reviewIntervals.getOrElse(progress.history.size) { Long.MAX_VALUE }
        // Son tekrar zamanını al, yoksa 0 kullan
        val lastTime = progress.history.lastOrNull() ?: 0L
        // Gerekli süre geçtiyse true döndür
        return now - lastTime >= requiredDelay
    }

    // Bugün tekrar edilecek kelimeleri al
    private fun getTodaysQuizWords(): List<Word> {
        return wordList.filter { word ->
            val progress = progressMap[word.createdBy] ?: return@filter false
            isEligibleForReview(progress)
        }
    }

    // Sonraki soruyu göster
    private fun showNextQuestion() {
        binding.feedbackText.text = "" // Geri bildirim metnini temizle
        binding.nextImage.visibility = View.GONE // Sonraki butonu gizle

        currentWord = quizWords[currentIndex] // Şu anki kelimeyi al
        correctAnswer = currentWord.turWord // Doğru cevabı belirle

        binding.questionText.text = currentWord.engWord // İngilizce kelimeyi göster
        binding.questionImage.setImageResource(currentWord.imageResId) // Kelime resmini göster
        binding.questionText1.text = "Question ${currentIndex + 1}/$maxDailyWords" // Soru numarasını göster

        val options = generateOptions(correctAnswer) // Seçenekleri oluştur
        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
        )

        optionButtons.forEachIndexed { index, button ->
            button.text = options[index] // Seçenek metnini ayarla
            button.setBackgroundColor(Color.LTGRAY) // Buton rengini sıfırla
            button.isEnabled = true // Butonları tekrar etkinleştir
        }
    }

    // Cevabı kontrol et
    private fun checkAnswer(selected: String) {
        val isCorrect = selected == correctAnswer // Cevap doğru mu?
        val progress = progressMap[currentWord.createdBy] ?: return // Kelimenin ilerlemesini al

        if (isCorrect) {
            binding.feedbackText.text = "Doğru!"
            correctAnswersCount++ // Doğru sayısını artır!
            progress.history.add(System.currentTimeMillis()) // Geçmişe zaman damgası ekle
            // Geçmiş listesini en fazla 6 elemanla sınırlayalım
            if (progress.history.size > 6) {
                progress.history = progress.history.takeLast(6).toMutableList()
            }
        } else {
            binding.feedbackText.text = "Yanlış! Doğru cevap: $correctAnswer"
            incorrectAnswersCount++ // Yanlış sayısını artır!
            progress.history.clear() // Yanlışsa geçmişi sıfırla
        }

        saveProgressToFirestore(currentWord.createdBy, progress) // İlerlemeyi Firestore'a kaydet

        // Tüm seçenek butonlarını devre dışı bırak ve renklerini güncelle
        val optionButtons = listOf(
            binding.option1, binding.option2, binding.option3, binding.option4
        )

        optionButtons.forEach { button ->
            button.isEnabled = false // Butonları devre dışı bırak
            button.setBackgroundColor(
                when (button.text) {
                    correctAnswer -> Color.GREEN // Doğru cevap yeşil
                    selected -> Color.RED // Seçilen cevap yanlışsa kırmızı
                    else -> Color.LTGRAY // Diğerleri gri
                }
            )
        }

        binding.nextImage.visibility = View.VISIBLE // Sonraki butonu görünür yap
    }

    // Seçenekleri oluştur
    private fun generateOptions(correct: String): List<String> {
        val options = mutableSetOf(correct) // Doğru cevabı ekle
        while (options.size < 4) {
            val randomOption = wordList.random().turWord // Rastgele bir Türkçe kelime al
            options.add(randomOption) // Seçeneklere ekle
        }
        return options.shuffled() // Seçenekleri karıştır
    }

    // İlerlemeyi Firestore'dan yükle
    private fun loadProgressFromFirestore() {
        if (currentUserId == null) {
            initializeProgress()
            startQuizWithLimit()
            return
        }

        firestore.collection("Users")
            .document(currentUserId)
            .collection("WordProgress")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val progress = document.toObject(WordProgress::class.java)
                    progressMap[progress.wordId] = progress // Yüklenen ilerlemeyi haritaya ekle
                }
                initializeProgress() // Haritada eksik kelimeler varsa ilklendir
                startQuizWithLimit() // Quiz'i başlat
            }
            .addOnFailureListener {
                initializeProgress() // Yükleme hatası olursa da ilklendir
                startQuizWithLimit() // Quiz'i başlat
            }
    }

    // İlerlemeyi Firestore'a kaydet
    private fun saveProgressToFirestore(wordId: String, progress: WordProgress) {
        if (currentUserId == null) return

        firestore.collection("Users")
            .document(currentUserId)
            .collection("WordProgress")
            .document(wordId)
            .set(progress) // İlerlemeyi kaydet veya güncelle
    }
}

package com.example.suniyintelect

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.suniyintelect.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var questionTextViews: Array<TextView?>
    private lateinit var answersTextView: Array<TextView?>
    private var speechRecognizer: SpeechRecognizer? = null
    private var previousQuestions: MutableList<String>? = null
    private var textToSpeech: TextToSpeech? = null

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech!!.setLanguage(Locale.getDefault())
                if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                    languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Log.e("TextToSpeech", "Language is not supported or missing data")
                } else {
                    // Всё успешно и язык поддерживается
                    Log.i("TextToSpeech", "TextToSpeech initialized successfully")
                    // Вы можете добавить здесь свой дополнительный код
                }
            }
        }
        questionTextViews = arrayOfNulls(2)

        questionTextViews[0] = binding.botSoz
        questionTextViews[1] = binding.btoSozz

        answersTextView = arrayOfNulls(2)

        answersTextView[0] = binding.tvSoz
        answersTextView[1] = binding.tvSozz


        binding.btnBotton.setOnClickListener {

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            try {
                startActivityForResult(intent, 1)
            } catch (a: ActivityNotFoundException) {
                Toast.makeText(this, "Error ${a.message}", Toast.LENGTH_SHORT).show()
            }
        }
        previousQuestions = ArrayList()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val result = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && result.size > 0) {
                val question = result[0]


                for (i in 0 until questionTextViews.size) {
                    if (i == previousQuestions!!.size) {
                        previousQuestions!!.add(question)
                        questionTextViews[i]!!.text = question
                        break
                    }
                }

                gen(question)

            }
        }
    }

    private fun generateAnswer(question: String): String {

        return when (question) {
            "salom" -> "Asslomu aleykum"
            "mening ismim hojiakbar" -> "Labbay Hojiakbar"
            else -> "Kechirasiz. Tushunmadim"
        }
    }

    private fun gen(question: String){
        val answer = generateAnswer(question)
        answersTextView.get(previousQuestions!!.size - 1)?.setText(answer)

        textToSpeech!!.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
        }
    }
}

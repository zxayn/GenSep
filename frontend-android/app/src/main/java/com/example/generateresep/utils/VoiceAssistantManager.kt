package com.example.generateresep.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class VoiceAssistantManager(
    private val context: Context,
    private val onCommand: (String) -> Unit
) : RecognitionListener {

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isTtsInitialized = false
    private var isActive = false 

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("id", "ID")
                isTtsInitialized = true
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(this)
    }

    fun speak(text: String, onDone: () -> Unit = {}) {
        if (isTtsInitialized) {
            stopListeningInternal()
            Log.d("VoiceAssistant", "Speaking: $text")
            
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "recipe_step")
            
            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("VoiceAssistant", "TTS started speaking")
                }
                override fun onDone(utteranceId: String?) {
                    Log.d("VoiceAssistant", "TTS finished speaking, triggering onDone")
                    (context as? android.app.Activity)?.runOnUiThread {
                        onDone()
                    } ?: onDone()
                }
                override fun onError(utteranceId: String?) {
                    Log.e("VoiceAssistant", "TTS error occurred")
                }
            })

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "recipe_step")
        } else {
            Log.e("VoiceAssistant", "TTS not initialized yet")
        }
    }

    fun startListening() {
        isActive = true
        Log.d("VoiceAssistant", "startListening() called")
        startListeningInternal()
    }

    private fun startListeningInternal() {
        Log.d("VoiceAssistant", "startListeningInternal() executing")
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            // Penting: Meminta hasil parsial agar lebih responsif
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        
        (context as? android.app.Activity)?.runOnUiThread {
            Log.d("VoiceAssistant", "Calling speechRecognizer.startListening")
            speechRecognizer?.startListening(intent)
        }
    }

    fun stopListening() {
        isActive = false
        stopListeningInternal()
    }

    private fun stopListeningInternal() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
    }

    fun destroy() {
        isActive = false
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("VoiceAssistant", "Ready for speech")
    }

    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}

    @Suppress("DEPRECATION")
    override fun onError(error: Int) {
        if (!isActive) return
        Log.e("VoiceAssistant", "Error: $error")
        
        if (error == SpeechRecognizer.ERROR_NO_MATCH || 
            error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
            error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
            startListeningInternal()
        }
    }

    override fun onResults(results: Bundle?) {
        if (!isActive) return
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d("VoiceAssistant", "Results: $matches")
        matches?.let {
            for (match in it) {
                val command = match.lowercase(Locale.getDefault())
                if (command.contains("lanjut") || command.contains("next") || command.contains("berikutnya")) {
                    onCommand("lanjut")
                    return
                } else if (command.contains("ulang") || command.contains("repeat") || command.contains("ulangi")) {
                    onCommand("ulang")
                    return
                }
            }
        }
        if (isActive) startListeningInternal()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d("VoiceAssistant", "Partial Results: $matches")
        matches?.let {
            for (match in it) {
                val command = match.lowercase(Locale.getDefault())
                if (command.contains("lanjut") || command.contains("next") || command.contains("berikutnya")) {
                    Log.d("VoiceAssistant", "Partial command detected: lanjut")
                    onCommand("lanjut")
                    stopListeningInternal() // Berhenti agar tidak terpicu ganda
                    return
                } else if (command.contains("ulang") || command.contains("repeat") || command.contains("ulangi")) {
                    Log.d("VoiceAssistant", "Partial command detected: ulang")
                    onCommand("ulang")
                    stopListeningInternal()
                    return
                }
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}

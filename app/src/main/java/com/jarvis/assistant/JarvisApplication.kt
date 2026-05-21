package com.jarvis.assistant

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class JarvisApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    var textToSpeech: TextToSpeech? = null
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        initTextToSpeech()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onTerminate() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onTerminate()
    }

    companion object {
        lateinit var instance: JarvisApplication
            private set
    }
}

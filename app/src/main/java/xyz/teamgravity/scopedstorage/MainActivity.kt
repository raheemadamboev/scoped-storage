package xyz.teamgravity.scopedstorage

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.scopedstorage.databinding.ActivityMainBinding
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var audioPickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var exoplayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launcher()
        lateInIt()
        button()
    }

    private fun launcher() {
        audioPickerLauncher()
        imagePickerLauncher()
    }

    private fun lateInIt() {
        exoplayer = ExoPlayer.Builder(this).build()
        exoplayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
        exoplayer.setHandleAudioBecomingNoisy(true)
    }

    private fun audioPickerLauncher() {
        audioPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK || result.data?.data == null) return@registerForActivityResult

            lifecycleScope.launch {
                exoplayer.apply {
                    setMediaItem(MediaItem.fromUri(result.data?.data!!))
                    prepare()
                    play()
                    delay(5000)
                    stop()
                }
            }
        }
    }

    private fun imagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK || result.data?.data == null) return@registerForActivityResult

            Glide.with(this).load(result.data?.data!!).into(binding.chooseImageI)
        }
    }

    private fun button() {
        onChooseAudio()
        onChooseImage()
        onCreateFileDownloads()
    }

    private fun onChooseAudio() {
        binding.chooseAudioB.setOnClickListener {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
                audioPickerLauncher.launch(this)
            }
        }
    }

    private fun onChooseImage() {
        binding.chooseImageB.setOnClickListener {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
                imagePickerLauncher.launch(this)
            }
        }
    }

    private fun onCreateFileDownloads() {
        binding.createFileDownloadB.setOnClickListener {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val raheem = File(downloads, "raheem")
            if (!raheem.exists()) raheem.mkdir()
            val randomFile = File(raheem, "${Random().nextInt(9999)}.txt")
            randomFile.writeText("Hello Android!")
        }
    }
}
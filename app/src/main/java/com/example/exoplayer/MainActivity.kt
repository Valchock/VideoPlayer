package com.example.exoplayer


import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.exoplayer.databinding.ActivityPlayerBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var exoPlayer: ExoPlayer
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private val playbackStateListener : Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 ) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23 ) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23 ) {
            releasePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(){
        //var videoPath = "/storage/emulated/0/Download/SampleVideo_1.mp4"
        var videoUri = Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.videoView.player = exoPlayer
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)

        val secondMediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3")
        exoPlayer.addMediaItem(secondMediaItem)

        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.seekTo(currentItem, playbackPosition)
        exoPlayer.addListener(playbackStateListener)
        exoPlayer.prepare()
        exoPlayer.play()

    }

    private fun releasePlayer() {
        playWhenReady = exoPlayer.playWhenReady
        currentItem = exoPlayer.currentMediaItemIndex
        playbackPosition = exoPlayer.currentPosition
        exoPlayer.removeListener(playbackStateListener)
        exoPlayer.release()

    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString : String = when(playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer state idle"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer state idle"
                ExoPlayer.STATE_READY -> "ExoPlayer state ready"
                ExoPlayer.STATE_ENDED -> "ExoPlayer state ended"
                else -> "Unknown State"
            }
            Log.d("Player state", "State change to" + stateString)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val playingString : String = if(isPlaying) "Playing" else "Not Playing"
            Log.d("Player state", "Player is currently" + playingString)
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if(events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)){
                exoPlayer.seekTo(5000L)
            }
        }

    }

}


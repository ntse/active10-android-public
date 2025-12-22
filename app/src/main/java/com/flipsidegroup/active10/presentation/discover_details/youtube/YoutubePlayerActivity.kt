package com.flipsidegroup.active10.presentation.discover_details.youtube

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.flipsidegroup.active10.databinding.ActivityYoutubePlayerBinding
import com.flipsidegroup.active10.presentation.common.activities.ToolbarActivity
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback

const val PARAM_YOUTUBE_VIDEO_ID = "PARAM_YOUTUBE_VIDEO_ID"

fun Context.YoutubePlayerIntent(videoId: String): Intent {
    return Intent(this, YoutubePlayerActivity::class.java).apply {
        putExtra(PARAM_YOUTUBE_VIDEO_ID, videoId)
    }
}

class YoutubePlayerActivity : ToolbarActivity() {

    private var binding: ActivityYoutubePlayerBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityYoutubePlayerBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        binding.youtubeToolbar.backTV.setOnClickListener { onBackPressed() }

        val videoId = intent.getStringExtra(PARAM_YOUTUBE_VIDEO_ID) ?: ""


        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

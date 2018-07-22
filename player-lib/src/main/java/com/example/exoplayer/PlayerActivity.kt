/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener


/**
 * A fullscreen activity to play audio or video streams.
 */
class PlayerActivity : AppCompatActivity() {

    private var playerView: PlayerView? = null
    private var player: SimpleExoPlayer? = null

    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    private var componentListener: ComponentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerView = findViewById(R.id.video_view)

        componentListener = ComponentListener()
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)

            player = ExoPlayerFactory.newSimpleInstance(
                    DefaultRenderersFactory(this),
                    DefaultTrackSelector(adaptiveTrackSelectionFactory),
                    DefaultLoadControl())

            player!!.addListener(componentListener)
            player!!.addVideoDebugListener(componentListener as VideoRendererEventListener?)
            player!!.addAudioDebugListener(componentListener as AudioRendererEventListener?)
        }

        playerView!!.player = player

        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)

        val uri = Uri.parse(getString(R.string.media_url_dash))
        val mediaSource = buildMediaSource(uri)
        player!!.prepare(mediaSource, true, false)
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.removeListener(componentListener)
            player!!.setVideoListener(null)
            player!!.removeVideoDebugListener(componentListener as VideoRendererEventListener?)
            player!!.removeAudioDebugListener(componentListener as AudioRendererEventListener?)
            player!!.release()
            player = null
        }
    }

    //  private MediaSource buildMediaSource(Uri uri) {
    //    return new ExtractorMediaSource.Factory(
    //            new DefaultHttpDataSourceFactory("exoplayer-codelab")).
    //            createMediaSource(uri);
    //  }

    //  private MediaSource buildMediaSource(Uri uri) {
    //    // these are reused for both media sources we create below
    //    DefaultExtractorsFactory extractorsFactory =
    //            new DefaultExtractorsFactory();
    //    DefaultHttpDataSourceFactory dataSourceFactory =
    //            new DefaultHttpDataSourceFactory( "user-agent");
    //
    //    ExtractorMediaSource videoSource =
    //            new ExtractorMediaSource.Factory(
    //                    new DefaultHttpDataSourceFactory("exoplayer-codelab")).
    //                    createMediaSource(uri);
    //
    //    Uri audioUri = Uri.parse(getString(R.string.media_url_mp3));
    //    ExtractorMediaSource audioSource =
    //            new ExtractorMediaSource.Factory(
    //                    new DefaultHttpDataSourceFactory("exoplayer-codelab")).
    //                    createMediaSource(audioUri);
    //
    //    return new ConcatenatingMediaSource(audioSource, videoSource);
    //  }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val manifestDataSourceFactory = DefaultHttpDataSourceFactory("ua")
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER))
        return DashMediaSource.Factory(dashChunkSourceFactory,
                manifestDataSourceFactory).createMediaSource(uri)
    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    companion object {

        private val BANDWIDTH_METER = DefaultBandwidthMeter()
    }

}

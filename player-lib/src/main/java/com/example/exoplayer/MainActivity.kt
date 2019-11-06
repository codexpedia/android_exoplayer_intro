package com.example.exoplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val MEDIA_URI = "media_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_mp3.setOnClickListener {
            et_url.setText(getString(R.string.media_url_mp3))
        }

        btn_mp4.setOnClickListener {
            et_url.setText(getString(R.string.media_url_mp4))
        }

        btn_live.setOnClickListener {
            et_url.setText(getString(R.string.media_url_dash))
        }

        btn_play.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(MEDIA_URI, et_url.text.toString())
            startActivity(intent)
        }
    }

}
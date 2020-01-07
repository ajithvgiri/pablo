package com.ajithvgiri.pablodemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.ajithvgiri.pablo.Pablo
import kotlinx.android.synthetic.main.activity_fullscreen.*

class FullscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imageUrl = intent.getStringExtra("image")

        val pablo = Pablo(this)
        pablo.isCompress = false // for loading original image
        pablo.displayImage(imageUrl, fullscreen_imageView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

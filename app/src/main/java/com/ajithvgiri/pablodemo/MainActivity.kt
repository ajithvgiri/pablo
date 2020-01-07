package com.ajithvgiri.pablodemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.pablo.Pablo
import com.ajithvgiri.pablodemo.adapter.ImageAdapter
import com.ajithvgiri.pablodemo.adapter.OnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    val images = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        readJSONFromAsset()?.let {
            val reader = JSONObject(it)
            val imagesArray = reader.getJSONArray("images")
            for (i in 0 until imagesArray.length()) {
                images.add(imagesArray.getJSONObject(i)["download_url"].toString())
            }
        }


        // Pablo is the library for loading images
        val pablo = Pablo(this)
        val adapter = ImageAdapter(images, pablo, object : OnClickListener {
            override fun onClicked(imageUrl: String) {
                val intent = Intent(this@MainActivity, FullscreenActivity::class.java)
                intent.putExtra("image", imageUrl)
                startActivity(intent)
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }


    private fun readJSONFromAsset(): String? {
        val json: String?
        json = try {
            val inputStream: InputStream = assets.open("images.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}

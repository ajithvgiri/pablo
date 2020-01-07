package com.ajithvgiri.pablo

import android.content.Context
import java.io.File

class FileCache(context: Context) {
    private var cacheDir: File? = null
    fun getFile(url: String): File { //Identify images by hashcode or encode by URLEncoder.encode.
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)
    }

    fun clear() { // list all files inside cache directory
        val files = cacheDir!!.listFiles() ?: return
        //delete all cache directory files
        for (f in files) f.delete()
    }

    init {
        cacheDir = context.cacheDir
        if (!cacheDir!!.exists()) { // create cache dir in your application context
            cacheDir!!.mkdirs()
        }
    }
}
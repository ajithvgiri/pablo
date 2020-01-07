package com.ajithvgiri.pablo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Pablo(context: Context) {

    var isCompress = true
    // Initialize MemoryCache
    var memoryCache = MemoryCache()
    private var fileCache: FileCache = FileCache(context)

    //Create Map (collection) to store image and image url in key value pair
    private val imageViews = Collections.synchronizedMap(WeakHashMap<ImageView, String>())

    // Creates a thread pool that reuses a fixed number of
    // threads operating off a shared unbounded queue.
    private var executorService: ExecutorService = Executors.newFixedThreadPool(5)

    //handler to display images in UI thread
    var handler = Handler()

    // default image show in list (Before online image download)
    val placeHolder = R.drawable.default_placeholder

    fun displayImage(url: String, imageView: ImageView) {
        //Store image and url in Map
        imageViews[imageView] = url

        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        val bitmap = memoryCache[url]

        if (bitmap != null) {
            // if image is stored in MemoryCache Map then
            // Show image in gridView
            imageView.setImageBitmap(bitmap)
        } else {
            //queue Photo to download from url
            queuePhoto(url, imageView)
            //Before downloading image show default image
            imageView.setImageResource(placeHolder)
        }
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        // Store image and url in PhotoToLoad object
        val p = PhotoToLoad(url, imageView)
        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution
        executorService.submit(PhotosLoader(p))
    }

    //Task for the queue
    inner class PhotoToLoad(var url: String, var imageView: ImageView)

    inner class PhotosLoader(private var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            try {
                //Check if image already downloaded
                if (imageViewReused(photoToLoad)) return
                // download image from web url
                val bmp = getBitmap(photoToLoad.url)
                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp)
                if (imageViewReused(photoToLoad)) return
                // Get bitmap to display
                val bd = BitmapDisplayer(bmp, photoToLoad)
                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd)
            } catch (th: Throwable) {
                Log.e("ImageLoader", "exception from PhotosLoader ${th.localizedMessage}")
                th.printStackTrace()
            }
        }

    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)
        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        val b = decodeFile(f)
        return b ?: try {
            val imageUrl = URL(url)
            val conn = imageUrl.openConnection() as HttpURLConnection
            conn.connectTimeout = 30000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true
            val inputStream = conn.inputStream
            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            val os: OutputStream = FileOutputStream(f)
            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            copyStream(inputStream, os)
            os.close()
            conn.disconnect()
            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            val bitmap: Bitmap? = decodeFile(f)
            bitmap
        } catch (ex: Throwable) {
            ex.printStackTrace()
            if (ex is OutOfMemoryError) memoryCache.clear()
            null
        }
        // Download image file from web
    }

    private fun copyStream(inputStream: InputStream, os: OutputStream) {
        val bufferSize = 1024
        try {
            val bytes = ByteArray(bufferSize)
            while (true) {
                //Read byte from input stream
                val count = inputStream.read(bytes, 0, bufferSize)
                if (count == -1) break
                //Write byte from output stream
                os.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
            Log.e("Utils", "Exception in ${ex.localizedMessage}")
        }
    }

    //Decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File): Bitmap? {
        try {
            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()
            //Find the correct scale value. It should be the power of 2.
            // Set width/height of recreated image
            val requiredSize = 100
            var widthTmp = o.outWidth
            var heightTmp = o.outHeight
            var scale = 1
            while (isCompress) {
                if (widthTmp / 2 < requiredSize || heightTmp / 2 < requiredSize) break
                widthTmp /= 2
                heightTmp /= 2
                scale *= 2
            }
            //decode with current scale values
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: FileNotFoundException) {
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        //Check url is already exist in imageViews MAP
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    inner class BitmapDisplayer(private var bitmap: Bitmap?, private var photoToLoad: PhotoToLoad) :
        Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad)) return
            // Show bitmap on UI
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap) else photoToLoad.imageView.setImageResource(
                placeHolder
            )
        }

    }

    fun clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear()
        fileCache.clear()
    }
}
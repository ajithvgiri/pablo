package com.ajithvgiri.pablodemo.network

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

open class HttpRequestTask : AsyncTask<String, Int, String>() {

    override fun doInBackground(vararg params: String): String? {
        val requestUrl = params[0]
        try {
            val url = URL(requestUrl)
            val httpURLConnection =
                url.openConnection() as HttpURLConnection
            // setting the  Request Method Type
            httpURLConnection.requestMethod = "GET"
            // adding the headers for request
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            try { //to tell the connection object that we will be wrting some data on the server and then will fetch the output result
                httpURLConnection.doOutput = true
                // this is used for just in case we don't know about the data size associated with our request
                httpURLConnection.setChunkedStreamingMode(0)
                // to write tha data in our request
                val outputStream: OutputStream =
                    BufferedOutputStream(httpURLConnection.outputStream)
                val outputStreamWriter = OutputStreamWriter(outputStream)
                outputStreamWriter.flush()
                outputStreamWriter.close()
                // to log the response code of your request
                Log.d(
                    HttpRequestTask::class.java.simpleName,
                    "HttpRequestTask doInBackground : " + httpURLConnection.responseCode
                )
                // to log the response message from your server after you have tried the request.
                Log.d(
                    HttpRequestTask::class.java.simpleName,
                    "HttpRequestTask doInBackground : " + httpURLConnection.responseMessage
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally { // this is done so that there are no open connections left when this task is going to complete
                httpURLConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
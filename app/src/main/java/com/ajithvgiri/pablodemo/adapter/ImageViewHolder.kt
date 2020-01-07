package com.ajithvgiri.pablodemo.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.pablo.Pablo
import kotlinx.android.synthetic.main.recycler_image.view.*

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindToView(imageUrl: String, pablo: Pablo, clickListener: () -> Unit) {
        pablo.displayImage(imageUrl, itemView.imageView)
        itemView.imageView.setOnClickListener {
            clickListener()
        }
    }
}
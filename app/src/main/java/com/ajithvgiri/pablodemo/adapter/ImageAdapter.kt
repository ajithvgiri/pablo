package com.ajithvgiri.pablodemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.pablo.Pablo
import com.ajithvgiri.pablodemo.R


class ImageAdapter(
    var list: ArrayList<String>,
    var pablo: Pablo,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_image,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageViewHolder).bindToView(list[position], pablo) {
            onClickListener.onClicked(list[position])
        }
    }

}
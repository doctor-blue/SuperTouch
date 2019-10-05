package com.doctor.blue.supertouch.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.model.ItemTouch
import java.io.File

class IconAdapter(val listPath:List<String>,val context: Context, private val itemClick: (String) -> Unit) :
    RecyclerView.Adapter<IconAdapter.IconViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.layout_floating_view, parent,false)
        return IconViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return listPath.size
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class IconViewHolder(itemView: View, val itemClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imgIcon: ImageView = itemView.findViewById(R.id.btn_floating_button)
        fun onBind(position: Int) {
            Glide.with(context)
                .load(Uri.fromFile(File("//android_asset/image/" + listPath[position])))
                .apply(
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(64, 64)
                )
                .into(imgIcon)
            imgIcon.setOnClickListener{
                itemClick(listPath[position])
            }
        }
    }
}
package com.doctor.blue.supertouch.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.doctor.blue.supertouch.R

class ApplicationAdapter(
    val context: Context,
    val listPackage: List<String>,
    private val itemClick: (String) -> Unit
) : RecyclerView.Adapter<ApplicationAdapter.ViewHolder>() {


    private var checkItemIndex = 0
    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_application, null)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return listPackage.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(position)
    }

    inner class ViewHolder(itemView: View, val itemClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imgIconApp: ImageView = itemView.findViewById(R.id.img_icon_app)
        private val imgCheckApp: ImageView = itemView.findViewById(R.id.img_check_app)

        @SuppressLint("ResourceType")
        fun onBindView(position: Int) {
            val packageName = listPackage[position]
            imgIconApp.setImageDrawable(context.packageManager.getApplicationIcon(packageName))
            if (checkItemIndex == position)
                imgCheckApp.visibility = View.VISIBLE
            else
                imgCheckApp.visibility = View.INVISIBLE
            imgIconApp.setOnClickListener {
                checkItemIndex = position
                notifyDataSetChanged()
                itemClick(packageName)
            }
        }
    }
}
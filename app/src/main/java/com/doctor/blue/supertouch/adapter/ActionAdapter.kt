package com.doctor.blue.supertouch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doctor.blue.supertouch.R
import com.doctor.blue.supertouch.database.SuperTouchDatabase
import com.doctor.blue.supertouch.model.ItemTouch

class ActionAdapter(val context: Context, private val itemClick: (ItemTouch) -> Unit) :
    RecyclerView.Adapter<ActionAdapter.ActionViewHolder>() {
    private val listAction = SuperTouchDatabase.listItemMain
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_action, parent,false)
        return ActionViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return listAction.size
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ActionViewHolder(itemView: View, val itemClick: (ItemTouch) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imgAction: ImageView = itemView.findViewById(R.id.img_action)
        private val txtNameAction: TextView = itemView.findViewById(R.id.txt_name_action)
        fun onBind(position: Int) {
            imgAction.setImageResource(listAction[position].iconItem)
            txtNameAction.text = context.resources.getString(listAction[position].nameItem)
            imgAction.setOnClickListener {
                itemClick(listAction[position])
            }
        }
    }
}
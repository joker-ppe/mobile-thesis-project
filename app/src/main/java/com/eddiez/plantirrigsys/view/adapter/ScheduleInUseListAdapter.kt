package com.eddiez.plantirrigsys.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.marcinorlowski.fonty.Fonty

class ScheduleInUseListAdapter(
    private val context: Context,
    private val items: List<ScheduleDataModel>
) : ArrayAdapter<ScheduleDataModel>(context, R.layout.layout_list_schedule_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_list_schedule_item, parent, false)

        val tvScheduleName = view.findViewById<TextView>(R.id.tvScheduleName)
        val imgSchedule = view.findViewById<ImageView>(R.id.imgSchedule)

        tvScheduleName.text = items[position].title
        Glide.with(parent).load(items[position].imageData)
            .placeholder(R.drawable.image_default)
            .error(R.drawable.image_default)
            .into(imgSchedule)

        if (position == items.size - 1) {
            view.findViewById<View>(R.id.divider).visibility = View.GONE
        } else {
            view.findViewById<View>(R.id.divider).visibility = View.VISIBLE
        }

        Fonty.setFonts(view as ViewGroup)

        return view
    }
}
package com.example.travelpal.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.models.Notification
import com.example.travelpal.models.Trip
import com.example.travelpal.ui.NotificationDetailsActivity
import com.example.travelpal.ui.TripDetailsActivity
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader

open class NotificationAdapter(private val context : Context, private var listOfNotification : ArrayList<Notification>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.notification_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listOfNotification[position]

        if (holder is MyViewHolder) {

            holder.header.text = model.notificationHeader
            holder.subHeader.text = model.notificationSubHeader
            holder.date.text = model.date

            holder.itemView.setOnClickListener {
                val intent = Intent(context, NotificationDetailsActivity::class.java)
                val activity = context as Activity
                intent.putExtra(Constants.EXTRA_NOTIFICATION, listOfNotification[position])

                var activityOption = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    Pair(holder.header, Constants.VIEW_NAME_HEADER_NOTIFICATION),
                    Pair(holder.subHeader, Constants.VIEW_NAME_HEADER_NOTIFICATION_SUB),
                    Pair(holder.date, Constants.VIEW_NAME_HEADER_NOTIFICATION_DATE)
                )

                ActivityCompat.startActivity(activity!!, intent, activityOption.toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfNotification.size
    }

    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var header : TextView
        var subHeader: TextView
        var date : TextView

        init {
            header = itemView.findViewById(R.id.tv_header)
            subHeader = itemView.findViewById(R.id.tv_sub_header)
            date = itemView.findViewById(R.id.tv_date)
        }
    }

}
package com.example.travelpal.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.example.travelpal.models.Trip
import com.example.travelpal.ui.TripDetailsActivity
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader

class TripAdapter(private val context : Context, private var listOfTrips: ArrayList<Trip>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.trip_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listOfTrips[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.tripImg,
                holder.itemImage
            )
            holder.title.text = model.tripName
            holder.fromDate.text = model.fromDate
            holder.toDate.text = model.toDate

            holder.itemView.setOnClickListener {
                val intent = Intent(context, TripDetailsActivity::class.java)
                val activity = context as Activity
                intent.putExtra(Constants.EXTRA_STAY_ID, model.stayId)

                var activityOption = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    Pair(holder.itemImage, Constants.VIEW_NAME_HEADER_IMAGE),
                    Pair(holder.title, Constants.VIEW_NAME_HEADER_TITLE),
                    Pair(holder.fromDate, Constants.VIEW_NAME_HEADER_FROM_DATE),
                    Pair(holder.toDate, Constants.VIEW_NAME_HEADER_TO_DATE)
                )

                ActivityCompat.startActivity(activity!!, intent, activityOption.toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfTrips.size
    }

    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var itemImage : ImageView
        var fromDate : TextView
        var title : TextView
        var toDate : TextView

        init {
            itemImage = itemView.findViewById(R.id.img_item)
            title = itemView.findViewById(R.id.tv_title)
            fromDate = itemView.findViewById(R.id.tv_from_date)
            toDate = itemView.findViewById(R.id.tv_to_date)
        }
    }
}
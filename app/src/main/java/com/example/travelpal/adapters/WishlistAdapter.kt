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
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay
import com.example.travelpal.ui.StayDetailsActivity
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader

open class WishlistAdapter(private val context : Context, private var listOfStays : ArrayList<Stay>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.stay_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listOfStays[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.stayImg,
                holder.itemImage
            )
            holder.title.text = model.title
            holder.location.text = model.location
            holder.description.text = model.description
            holder.price.text = model.price



            holder.itemView.setOnClickListener {
                val intent = Intent(context, StayDetailsActivity::class.java)
                val activity = context as Activity

                intent.putExtra(Constants.EXTRA_STAY_ID, model.stayId)

                var activityOption = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    Pair(holder.itemImage, Constants.VIEW_NAME_HEADER_IMAGE),
                    Pair(holder.title, Constants.VIEW_NAME_HEADER_TITLE),
                    Pair(holder.location, Constants.VIEW_NAME_HEADER_LOCATION),
                    Pair(holder.description, Constants.VIEW_NAME_HEADER_DESCRIPTION),
                    Pair(holder.price, Constants.VIEW_NAME_HEADER_PRICE)
                )

                ActivityCompat.startActivity(activity!!, intent, activityOption.toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return listOfStays.size
    }

    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        var itemImage : ImageView
        var location : TextView
        var title : TextView
        var description : TextView
        var price : TextView

        init {
            itemImage = itemView.findViewById(R.id.img_item)
            title = itemView.findViewById(R.id.tv_title)
            location = itemView.findViewById(R.id.tv_location)
            description = itemView.findViewById(R.id.tv_description)
            price = itemView.findViewById(R.id.tv_price)
        }
    }
}
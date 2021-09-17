package com.example.travelpal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.models.Stay
import com.example.travelpal.utils.GlideLoader

open class ExploreAdapter(private val context : Context, private var listOfStays : ArrayList<Stay>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

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
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
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

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, stay: Stay)
    }
}
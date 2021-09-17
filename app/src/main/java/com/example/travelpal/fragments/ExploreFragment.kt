package com.example.travelpal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.adapters.ExploreAdapter
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay
import com.example.travelpal.ui.StayDetailsActivity
import com.example.travelpal.utils.Constants
import org.w3c.dom.Text

class ExploreFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_explore, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        var root : View? = view
        getListOfStays(root as View)
    }

    private fun getListOfStays(view : View) {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        Firestore().getListOfStays(this, view)
    }

    fun successListOfStays(listOfStays : ArrayList<Stay>, view : View) {
        hideProgressDialog()
        var recyclerView = view.findViewById<RecyclerView>(R.id.rv_stays)
        var layoutManager = LinearLayoutManager(view.context)
        if (listOfStays.size > 0) {

            recyclerView.visibility = View.VISIBLE

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

            var adapter = ExploreAdapter(requireActivity(), listOfStays)
            recyclerView.adapter = adapter

            adapter.setOnClickListener(object :
                ExploreAdapter.OnClickListener {
                override fun onClick(position: Int, stay: Stay) {

                    val intent = Intent(context, StayDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_STAY_ID, stay.stayId)
                    intent.putExtra(Constants.EXTRA_STAY_OWNER_ID, stay.userId)

                    var activityOption = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity!!,
                        Pair(view.findViewById<ImageView>(R.id.img_item), Constants.VIEW_NAME_HEADER_IMAGE),
                        Pair(view.findViewById<TextView>(R.id.tv_title), Constants.VIEW_NAME_HEADER_TITLE),
                        Pair(view.findViewById<TextView>(R.id.tv_location), Constants.VIEW_NAME_HEADER_LOCATION),
                        Pair(view.findViewById<TextView>(R.id.tv_description), Constants.VIEW_NAME_HEADER_DESCRIPTION),
                        Pair(view.findViewById<TextView>(R.id.tv_price), Constants.VIEW_NAME_HEADER_PRICE)
                    )

                    ActivityCompat.startActivity(activity!!, intent, activityOption.toBundle())
                }
            })
        } else {
            recyclerView.visibility = View.GONE
        }
    }
}

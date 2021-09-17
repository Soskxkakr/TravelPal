package com.example.travelpal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.adapters.TripAdapter
import com.example.travelpal.adapters.WishlistAdapter
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Trip

class TripsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_trips, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        var root : View? = view
        getListOfTrips(root as View, Firestore().getCurrentUserID())
    }

    fun getListOfTrips(view : View, userId : String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getListOfTrips(this, view, userId)
    }

    fun getListOfTripsSuccess(listOfTrips : ArrayList<Trip>, view : View) {
        hideProgressDialog()
        var recyclerView = view.findViewById<RecyclerView>(R.id.rv_trips)
        var layoutManager = LinearLayoutManager(view.context)
        if (listOfTrips.size > 0) {

            recyclerView.visibility = View.VISIBLE

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

            var adapter = TripAdapter(requireActivity(), listOfTrips)
            recyclerView.adapter = adapter

        } else {
            recyclerView.visibility = View.GONE
        }
    }
}
package com.example.travelpal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.adapters.NotificationAdapter
import com.example.travelpal.adapters.TripAdapter
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Notification
import com.example.travelpal.models.Trip

class NotificationFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_notification, container, false)


        return root
    }

    override fun onResume() {
        super.onResume()
        var root : View? = view
        getListOfNotifications(root as View, Firestore().getCurrentUserID())
    }

    fun getListOfNotifications(view : View, userId : String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getListOfNotifications(this, view, userId)
    }

    fun getListOfNotificationsSuccess(listOfNotification : ArrayList<Notification>, view : View) {
        hideProgressDialog()
        var recyclerView = view.findViewById<RecyclerView>(R.id.rv_notifications)
        var layoutManager = LinearLayoutManager(view.context)
        if (listOfNotification.size > 0) {

            recyclerView.visibility = View.VISIBLE

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

            var adapter = NotificationAdapter(requireActivity(), listOfNotification)
            recyclerView.adapter = adapter

        } else {
            recyclerView.visibility = View.GONE
        }
    }
}
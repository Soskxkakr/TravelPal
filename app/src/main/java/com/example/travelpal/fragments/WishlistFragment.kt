package com.example.travelpal.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.R
import com.example.travelpal.adapters.WishlistAdapter
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay

class WishlistFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_wishlist, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        var root : View? = view
        getWishLists(root as View)
    }

    fun getWishLists(view : View) {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getWishlistsFromUsers(this, Firestore().getCurrentUserID(), view)
    }

    fun getWishListsFromUsersSuccess(listOfStayId : ArrayList<String>, view : View) {
        Firestore().getListOfStaysFromDocument(this, listOfStayId, view)
    }

    fun getListOfStaysFromDocumentSuccess(listOfStays : ArrayList<Stay>, view: View) {
        hideProgressDialog()

        var recyclerView = view.findViewById<RecyclerView>(R.id.rv_wishlist)
        var layoutManager = LinearLayoutManager(view.context)
        if (listOfStays.size > 0) {

            recyclerView.visibility = View.VISIBLE

            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)

            var adapter = WishlistAdapter(requireActivity(), listOfStays)
            recyclerView.adapter = adapter

        } else {
            recyclerView.visibility = View.GONE
        }
    }
}
package com.example.travelpal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.User
import com.example.travelpal.ui.AddStayActivity
import com.example.travelpal.ui.ChangeProfileDetailsActivity
import com.example.travelpal.ui.LoginActivity
import com.example.travelpal.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class ProfileFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mUserDetails : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_profile, container, false)

        getUserDetails()
        root.findViewById<Button>(R.id.btn_change_profile_details).setOnClickListener(this)
        root.findViewById<Button>(R.id.btn_host).setOnClickListener(this)
        root.findViewById<Button>(R.id.btn_logout).setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_change_profile_details -> {
                    startActivity(Intent(context, ChangeProfileDetailsActivity::class.java))
                }

                R.id.btn_host -> {
                    startActivity(Intent(context, AddStayActivity::class.java))
                }

                R.id.btn_logout -> {
                    logout()
                }
            }
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getUserDetails(this)
    }

    fun getUserDetailsSuccess(user : User) {
        hideProgressDialog()
        mUserDetails = user
        var view : View = requireView()

        GlideLoader(requireContext()).loadProductPicture(
            mUserDetails.image,
            view.findViewById<ImageView>(R.id.profile_image)
        )

        view.findViewById<TextView>(R.id.tv_username).text = mUserDetails.firstName + mUserDetails.lastName
        view.findViewById<TextView>(R.id.tv_email).text = mUserDetails.email
        view.findViewById<TextView>(R.id.tv_contact).text = mUserDetails.contactNo
    }
}
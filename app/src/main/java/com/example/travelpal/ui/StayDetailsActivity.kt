package com.example.travelpal.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader

class StayDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mStayDetails : Stay
    private var mStayId : String = ""
    private var mStayOwnerId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stay_details)

        setUpActionBar()

        findViewById<CheckBox>(R.id.cb_favorite).setOnClickListener(this)
        findViewById<Button>(R.id.btn_book).setOnClickListener(this)

        if (intent.hasExtra(Constants.EXTRA_STAY_ID)) {
            mStayId = intent.getStringExtra(Constants.EXTRA_STAY_ID)!!
        }
        mStayOwnerId = Firestore().getCurrentUserID()
        Log.i("Cookies", mStayOwnerId)

        getStayDetails()
        checkWishlist(mStayId, mStayOwnerId)
        ViewCompat.setTransitionName(findViewById(R.id.iv_stay_image), Constants.VIEW_NAME_HEADER_IMAGE)
        ViewCompat.setTransitionName(findViewById(R.id.tv_stay_detail), Constants.VIEW_NAME_HEADER_TITLE)
        ViewCompat.setTransitionName(findViewById(R.id.tv_location), Constants.VIEW_NAME_HEADER_LOCATION)
        ViewCompat.setTransitionName(findViewById(R.id.tv_description), Constants.VIEW_NAME_HEADER_DESCRIPTION)
        ViewCompat.setTransitionName(findViewById(R.id.tv_price), Constants.VIEW_NAME_HEADER_PRICE)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.cb_favorite -> {
                    if (findViewById<CheckBox>(R.id.cb_favorite).isChecked) {
                        addToWishilists()
                    } else {
                        removeFromWishlists()
                    }
                }

                R.id.btn_book -> {
                    val intent = Intent(this, BookingActivity::class.java)
                    intent.putExtra(Constants.EXTRA_STAY_ID, mStayId)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_stay_details_activity)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun getStayDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getStayDetails(this@StayDetailsActivity, mStayId)
    }

    fun stayDetailsSuccess(stay: Stay) {

        mStayDetails = stay

        // Populate the product details in the UI.
        GlideLoader(this).loadProductPicture(
            stay.stayImg,
            findViewById(R.id.iv_stay_image)
        )

        findViewById<TextView>(R.id.tv_stay_detail).text = stay.title
        findViewById<TextView>(R.id.tv_location).text = stay.location
        findViewById<TextView>(R.id.tv_description).text = stay.description
        findViewById<TextView>(R.id.tv_price).text = stay.price
        hideProgressDialog()
    }

    fun addToWishilists() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().addToWishlists(this, mStayOwnerId, mStayId)
    }

    fun removeFromWishlists() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().removeFromWishlists(this, mStayOwnerId, mStayId)
    }

    fun addToWishlistsSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.favorite_success), Toast.LENGTH_SHORT).show()
    }

    fun removeFromWishlistsSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show()
    }

    fun checkWishlist(stayId : String, stayOwnerId : String) {
        Firestore().checkWishlist(this, stayId, stayOwnerId)
    }

    fun checkWishListComplete(check : Boolean) {
        findViewById<CheckBox>(R.id.cb_favorite).isChecked = check
    }
}
package com.example.travelpal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay
import com.example.travelpal.models.Trip
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader

class TripDetailsActivity : BaseActivity() {

    private lateinit var mTripDetails : Trip
    private lateinit var mStayDetails : Stay
    private var mStayId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_STAY_ID)) {
            mStayId = intent.getStringExtra(Constants.EXTRA_STAY_ID)!!
        }

        getTripDetails()
        ViewCompat.setTransitionName(findViewById(R.id.iv_stay_image), Constants.VIEW_NAME_HEADER_IMAGE)
        ViewCompat.setTransitionName(findViewById(R.id.tv_stay_detail), Constants.VIEW_NAME_HEADER_TITLE)
        ViewCompat.setTransitionName(findViewById(R.id.tv_from_date), Constants.VIEW_NAME_HEADER_FROM_DATE)
        ViewCompat.setTransitionName(findViewById(R.id.tv_to_date), Constants.VIEW_NAME_HEADER_TO_DATE)
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_trip_details_activity)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun getTripDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getTripDetails(this, mStayId)
    }

    fun getTripDetailsSuccess(trip : Trip) {
        mTripDetails = trip
        Firestore().getStayDetails(this, mTripDetails.stayId)

        GlideLoader(this).loadProductPicture(
            trip.tripImg,
            findViewById(R.id.iv_stay_image)
        )

        findViewById<TextView>(R.id.tv_from_date).text = mTripDetails.fromDate
        findViewById<TextView>(R.id.tv_to_date).text = mTripDetails.toDate
        findViewById<TextView>(R.id.tv_guest_number).text = mTripDetails.noOfGuest
        findViewById<TextView>(R.id.tv_total).text = mTripDetails.totalPrice
    }

    fun getStayDetailsSuccess(stay : Stay) {
        mStayDetails = stay
        findViewById<TextView>(R.id.tv_stay_detail).text = mStayDetails.title
        findViewById<TextView>(R.id.tv_location).text = mStayDetails.location
        findViewById<TextView>(R.id.tv_description).text = mStayDetails.description
        hideProgressDialog()
    }
}
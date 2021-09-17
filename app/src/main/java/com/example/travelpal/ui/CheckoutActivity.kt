package com.example.travelpal.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Notification
import com.example.travelpal.models.Trip
import com.example.travelpal.utils.Constants
import java.util.*

class CheckoutActivity : BaseActivity(), View.OnClickListener {

    var mStayImg : String = ""
    var mStayName : String = ""
    var mStayId : String = ""
    var mFromDate : String = ""
    var mToDate : String = ""
    var mNoOfGuests : String = ""
    var mTotalAmount : String = ""
    var mStayLocation : String = ""

    var year = 0
    var month = 0
    var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_STAY_IMAGE)) {
            mStayImg = intent.getStringExtra(Constants.EXTRA_STAY_IMAGE)!!
        }

        if (intent.hasExtra(Constants.EXTRA_STAY_NAME)) {
            mStayName = intent.getStringExtra(Constants.EXTRA_STAY_NAME)!!
        }

        if (intent.hasExtra(Constants.EXTRA_STAY_ID)) {
            mStayId = intent.getStringExtra(Constants.EXTRA_STAY_ID)!!
        }

        if (intent.hasExtra(Constants.EXTRA_FROM_DATE)) {
            mFromDate = intent.getStringExtra(Constants.EXTRA_FROM_DATE)!!
        }

        if (intent.hasExtra(Constants.EXTRA_TO_DATE)) {
            mToDate = intent.getStringExtra(Constants.EXTRA_TO_DATE)!!
        }

        if (intent.hasExtra(Constants.EXTRA_NO_OF_GUESTS)) {
            mNoOfGuests = intent.getStringExtra(Constants.EXTRA_NO_OF_GUESTS)!!
        }

        if (intent.hasExtra(Constants.EXTRA_TOTAL_AMOUNT)) {
            mTotalAmount = intent.getStringExtra(Constants.EXTRA_TOTAL_AMOUNT)!!
            findViewById<TextView>(R.id.tv_total).text = mTotalAmount
        }

        if (intent.hasExtra(Constants.EXTRA_STAY_LOCATION)) {
            mStayLocation = intent.getStringExtra(Constants.EXTRA_STAY_LOCATION)!!
        }

        findViewById<Button>(R.id.btn_checkout).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_checkout -> {
                    if (validatePaymentDetails()) {
                        uploadTrip()
                    }
                }
            }
        }
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_checkout_activity)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validatePaymentDetails() : Boolean {
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_card_number).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_card_number), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_card_holder_name).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_card_holder), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_mm_yy).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_expiry), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_cvv).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_cvv), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun getDateToday() : String {
        var cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        return "${day}/${month+1}/${year}"
    }

    fun uploadTrip() {
        val username = this.getSharedPreferences(
            Constants.TRAVELPAL_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!
        val trip = Trip(
            mStayId,
            Firestore().getCurrentUserID(),
            mStayImg,
            mStayName,
            username,
            mFromDate,
            mToDate,
            mNoOfGuests,
            mTotalAmount
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().uploadTripDetails(this, trip)
    }

    fun uploadTripSuccess() {
        createNotification()
    }

    fun createNotification() {
        val notification = Notification(
            Firestore().getCurrentUserID(),
            Constants.BOOK_SUCCESS_HEADER,
            Constants.BOOKING_SUCCESS_SUB_HEADER + " " + mStayLocation + ".",
            getDateToday()
        )
        Firestore().createNotification(this, notification)
    }

    fun createNotificationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "Checkout Success!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }
}
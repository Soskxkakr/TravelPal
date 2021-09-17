package com.example.travelpal.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Notification
import com.example.travelpal.utils.Constants

class NotificationDetailsActivity : BaseActivity() {

    private lateinit var mNotification : Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        if (intent.hasExtra(Constants.EXTRA_NOTIFICATION)) {
            mNotification = intent.getParcelableExtra<Notification>(Constants.EXTRA_NOTIFICATION)!!
        }

        setUpActionBar()
        findViewById<TextView>(R.id.tv_header).text = mNotification.notificationHeader
        findViewById<TextView>(R.id.tv_sub_header).text = mNotification.notificationSubHeader
        findViewById<TextView>(R.id.tv_date).text = mNotification.date
        ViewCompat.setTransitionName(findViewById(R.id.tv_header), Constants.VIEW_NAME_HEADER_NOTIFICATION)
        ViewCompat.setTransitionName(findViewById(R.id.tv_sub_header), Constants.VIEW_NAME_HEADER_NOTIFICATION_SUB)
        ViewCompat.setTransitionName(findViewById(R.id.tv_date), Constants.VIEW_NAME_HEADER_NOTIFICATION_DATE)
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_notification_details)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }
}
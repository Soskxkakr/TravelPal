package com.example.travelpal.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Stay
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import android.text.TextWatcher as TextWatcher1

class BookingActivity : BaseActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mStayDetails : Stay

    var dpBtnId = 0
    var mStayId : String = ""

    var month = 1
    var year = 0
    var day = 0

    var numberOfGuests = 0
    var daysOfStay = 0
    var price = 0

    var startYear = Calendar.getInstance().get(Calendar.YEAR)
    var startDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    var startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    var endYear = Calendar.getInstance().get(Calendar.YEAR)
    var endDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    var endMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    @RequiresApi(Build.VERSION_CODES.O)
    var startDate = LocalDate.of(startYear, startMonth, startDay)

    @RequiresApi(Build.VERSION_CODES.O)
    var endDate = LocalDate.of(endYear, endMonth, endDay)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_STAY_ID)) {
            mStayId = intent.getStringExtra(Constants.EXTRA_STAY_ID)!!
        }

        getStayDetails()

        findViewById<Button>(R.id.btn_dp_booking_from).setText(getDateToday())
        findViewById<Button>(R.id.btn_dp_booking_to).setText(getDateToday())

        findViewById<EditText>(R.id.et_no_of_guests).addTextChangedListener(object:TextWatcher1 {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (TextUtils.isEmpty(findViewById<EditText>(R.id.et_no_of_guests).text.toString())) {
                    findViewById<TextView>(R.id.tv_guest_number).setText("0")
                    numberOfGuests = 0
                    calculateTotal()
                } else {
                    findViewById<TextView>(R.id.tv_guest_number).setText(p0)
                    numberOfGuests = p0.toString().toInt()
                    calculateTotal()
                }


            }
            override fun afterTextChanged(p0: Editable?) {

            }

        })

        findViewById<Button>(R.id.btn_dp_booking_from).setOnClickListener(this)
        findViewById<Button>(R.id.btn_dp_booking_to).setOnClickListener(this)
        findViewById<Button>(R.id.btn_proceed_to_payment).setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_dp_booking_from -> {
                    getDateToday()
                    setDpId(R.id.btn_dp_booking_from)
                    var datePickerDialog = DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,this, year, month, day)
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.show()
                }

                R.id.btn_dp_booking_to -> {
                    getDateToday()
                    setDpId(R.id.btn_dp_booking_to)
                    var datePickerDialog = DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,this, year, month, day)
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.show()
                }

                R.id.btn_proceed_to_payment -> {
                    var numberOfDays = ChronoUnit.DAYS.between(startDate, endDate).toString()
                    if (validateBookingDetails(numberOfDays)) {
                        val intent = Intent(this, CheckoutActivity::class.java)
                        intent.putExtra(Constants.EXTRA_STAY_IMAGE, mStayDetails.stayImg)
                        intent.putExtra(Constants.EXTRA_STAY_NAME, mStayDetails.title)
                        intent.putExtra(Constants.EXTRA_STAY_ID, mStayId)
                        intent.putExtra(Constants.EXTRA_FROM_DATE, findViewById<Button>(R.id.btn_dp_booking_from).text.toString())
                        intent.putExtra(Constants.EXTRA_TO_DATE, findViewById<Button>(R.id.btn_dp_booking_to).text.toString())
                        intent.putExtra(Constants.EXTRA_NO_OF_GUESTS, numberOfGuests.toString())
                        intent.putExtra(Constants.EXTRA_TOTAL_AMOUNT, findViewById<TextView>(R.id.tv_total).text.toString())
                        intent.putExtra(Constants.EXTRA_STAY_LOCATION, mStayDetails.location)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        var date = "${day} ${getMonthString(month+1)} ${year}"
        if (dpBtnId == R.id.btn_dp_booking_from) {
            startYear = year
            startDay = day
            startMonth = month + 1
            startDate = LocalDate.of(startYear, startMonth, startDay)
            var numberOfDays = ChronoUnit.DAYS.between(startDate, endDate).toString()
            findViewById<TextView>(R.id.tv_number_of_days).setText(numberOfDays)
            findViewById<Button>(R.id.btn_dp_booking_from).setText(date)
            daysOfStay = numberOfDays.toInt()
            calculateTotal()
        } else {
            endYear = year
            endDay = day
            endMonth = month + 1
            endDate = LocalDate.of(endYear, endMonth, endDay)
            var numberOfDays = ChronoUnit.DAYS.between(startDate, endDate).toString()
            findViewById<TextView>(R.id.tv_number_of_days).setText(numberOfDays)
            findViewById<Button>(R.id.btn_dp_booking_to).setText(date)
            daysOfStay = numberOfDays.toInt()
            calculateTotal()
        }
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_booking_details_activity)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setDpId(dpId : Int) {
        dpBtnId = dpId
    }

    private fun getDateToday() : String {
        var cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        return "$day ${getMonthString(month+1)} $year"
    }

    private fun getMonthString(month : Int) : String {
        if (month == 1) {
            return "JAN"
        }
        if (month == 2) {
            return "FEB"
        }
        if (month == 3) {
            return "MAR"
        }
        if (month == 4) {
            return "APR"
        }
        if (month == 5) {
            return "MAY"
        }
        if (month == 6) {
            return "JUN"
        }
        if (month == 7) {
            return "JUL"
        }
        if (month == 8) {
            return "AUG"
        }
        if (month == 9) {
            return "SEP"
        }
        if (month == 10) {
            return "OCT"
        }
        if (month == 11) {
            return "NOV"
        }
        if (month == 12) {
            return "DEC"
        }
        return "JAN"
    }

    private fun validateBookingDetails(numberOfDays : String) : Boolean {
        return when {
            numberOfDays.toInt() <= 0 -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_invalid_dates), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_no_of_guests).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_number_of_guests), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun calculateTotal() {
        var total = (daysOfStay * price) * numberOfGuests
        findViewById<TextView>(R.id.tv_total).text = total.toString()
    }

    fun getStayDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getStayDetails(this, mStayId)
    }

    fun stayDetailsSuccess(stay: Stay) {

        mStayDetails = stay

        // Populate the product details in the UI.
        GlideLoader(this).loadProductPicture(
            stay.stayImg,
            findViewById(R.id.iv_booking_image)
        )

        findViewById<TextView>(R.id.tv_stay_detail).text = stay.title
        findViewById<TextView>(R.id.tv_location).text = stay.location
        findViewById<TextView>(R.id.tv_description).text = stay.description
        findViewById<TextView>(R.id.tv_price).text = stay.price
        price = stay.price.toInt()
        hideProgressDialog()
    }
}
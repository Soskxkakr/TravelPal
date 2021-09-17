package com.example.travelpal.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Notification
import com.example.travelpal.models.Stay
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader
import java.io.IOException
import java.util.*

class AddStayActivity : BaseActivity(), View.OnClickListener {
    // A global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for uploaded product image URL.
    private var mStayImageURL: String = ""

    var year = 0
    var month = 0
    var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_stay)

        findViewById<ImageView>(R.id.iv_add_stay_img).setOnClickListener(this)
        findViewById<Button>(R.id.btn_submit).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_stay_img -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_submit -> {
                    if (validateStayDetails()) {
                        uploadStayImg()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == 2
            && data!!.data != null) {

            // Replace the add icon with edit icon once the image is selected.
            findViewById<ImageView>(R.id.iv_add_stay_img).setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.edit
                )
            )

            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the product image in the ImageView.
                GlideLoader(this).loadProductPicture(
                    mSelectedImageFileUri!!,
                    findViewById<ImageView>(R.id.iv_stay_image)
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun validateStayDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_stay_img), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_stay_detail).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_stay_details), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_location).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_stay_location), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_description).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_stay_description), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_price).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_stay_price), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadStayImg() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.PRODUCT_IMAGE)
    }

    private fun uploadStayDetails() {
        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username = this.getSharedPreferences(
            Constants.TRAVELPAL_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!

        // Here we get the text from editText and trim the space
        val stay = Stay(
            Firestore().getCurrentUserID(),
            username,
            mStayImageURL,
            findViewById<EditText>(R.id.et_stay_detail).text.toString().trim { it <= ' ' },
            findViewById<EditText>(R.id.et_location).text.toString().trim { it <= ' ' },
            findViewById<EditText>(R.id.et_description).text.toString().trim { it <= ' ' },
            findViewById<EditText>(R.id.et_price).text.toString().trim { it <= ' ' },
        )
        Firestore().uploadStayDetails(this, stay)
    }

    private fun getDateToday() : String {
        var cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        return "${day}/${month+1}/${year}"
    }

    fun imageUploadSuccess(imageURL: String) {
        // Initialize the global image url variable.
        mStayImageURL = imageURL
        uploadStayDetails()
    }

    fun stayUploadSuccess() {
        createNotification()
    }

    fun createNotification() {
        val notification = Notification(
            Firestore().getCurrentUserID(),
            Constants.ADD_STAY_SUCCESS_HEADER,
            Constants.ADD_STAY_SUCCESS_SUB_HEADER + " " + findViewById<EditText>(R.id.et_stay_detail).text +
                    " on " + findViewById<EditText>(R.id.et_location).text + " for RM " + findViewById<EditText>(R.id.et_price).text,
            getDateToday()
        )
        Firestore().createNotification(this, notification)
    }

    fun createNotificationSuccess() {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.stay_upload_success), Toast.LENGTH_SHORT).show()
        finish()
    }
}
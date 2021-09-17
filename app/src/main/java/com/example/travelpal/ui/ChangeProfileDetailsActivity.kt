package com.example.travelpal.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.Notification
import com.example.travelpal.models.User
import com.example.travelpal.utils.Constants
import com.example.travelpal.utils.GlideLoader
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class ChangeProfileDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    var year = 0
    var month = 0
    var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile_details)

        setUpActionBar()
        getUserDetails()
        findViewById<ImageView>(R.id.iv_user_photo).setOnClickListener(this)
        findViewById<Button>(R.id.btn_save).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    } else {
                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {

                            Firestore().uploadImageToCloudStorage(
                                this,
                                mSelectedImageFileUri,
                                Constants.USER_PROFILE_IMAGE
                            )
                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            findViewById(R.id.iv_user_photo)
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun setUpActionBar() {
        var stayToolbar = findViewById<Toolbar>(R.id.toolbar_change_profile_activity)
        setSupportActionBar(stayToolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back)
        }

        stayToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateUserProfileDetails() : Boolean {
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_first_name).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_last_name).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(findViewById<EditText>(R.id.et_contact).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_contact), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val firstName = findViewById<EditText>(R.id.et_first_name).text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = findViewById<EditText>(R.id.et_last_name).text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }


        val mobileNumber = findViewById<EditText>(R.id.et_contact).text.toString().trim { it <= ' ' }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.contactNo.toString()) {
            userHashMap[Constants.CONTACT_NO] = mobileNumber.toString()
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        Firestore().updateUserProfileData(
            this,
            userHashMap
        )
    }

    private fun getDateToday() : String {
        var cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        return "${day}/${month+1}/${year}"
    }

    fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        Firestore().getUserDetails(this)
    }

    fun getUserDetailsSuccess(user : User) {
        hideProgressDialog()
        mUserDetails = user

        GlideLoader(this).loadProductPicture(
            mUserDetails.image,
            findViewById<ImageView>(R.id.iv_user_photo)
        )

        findViewById<EditText>(R.id.et_first_name).setText(mUserDetails.firstName)
        findViewById<EditText>(R.id.et_last_name).setText(mUserDetails.lastName)
        findViewById<TextView>(R.id.tv_email).text = mUserDetails.email
        findViewById<EditText>(R.id.et_contact).setText(mUserDetails.contactNo)
    }

    fun userProfileUpdateSuccess() {
        createNotification()
    }

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    fun createNotification() {
        val notification = Notification(
            Firestore().getCurrentUserID(),
            Constants.CHANGE_PROFILE_SUCCESS_HEADER,
            Constants.CHANGE_PROFILE_SUCCESS_SUB_HEADER,
            getDateToday()
        )
        Firestore().createNotification(this, notification)
    }

    fun createNotificationSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            "User profile has been updated!",
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
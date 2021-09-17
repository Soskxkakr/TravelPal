package com.example.travelpal.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    // Splash timer
    const val SPLASH_TIMER_MS : Long = 3000

    // Firebase collection
    const val USERS : String = "users"
    const val STAYS : String = "stays"
    const val TRIPS : String = "trips"
    const val NOTIFICATION : String = "notifications"

    // Shared preferences
    const val TRAVELPAL_PREFERENCES : String = "TravelPalPrefs"
    const val LOGGED_IN_USERNAME : String = "logged_in_user"

    const val VIEW_NAME_HEADER_IMAGE : String = "detail:header:image"
    const val VIEW_NAME_HEADER_TITLE : String = "detail:header:title"
    const val VIEW_NAME_HEADER_LOCATION : String = "detail:header:location"
    const val VIEW_NAME_HEADER_DESCRIPTION : String = "detail:header:description"
    const val VIEW_NAME_HEADER_PRICE : String = "detail:header:price"
    const val VIEW_NAME_HEADER_FROM_DATE : String = "detail:header:fromDate"
    const val VIEW_NAME_HEADER_TO_DATE : String = "detail:header:toDate"
    const val VIEW_NAME_HEADER_NOTIFICATION : String = "detail:header:notificationHeader"
    const val VIEW_NAME_HEADER_NOTIFICATION_SUB : String = "detail:header:notificationSubHeader"
    const val VIEW_NAME_HEADER_NOTIFICATION_DATE : String = "detail:header:notificationDate"

    // Extras
    const val EXTRA_STAY_ID : String = "extra_product_id"
    const val EXTRA_STAY_OWNER_ID : String = "extra_stay_owner_id"
    const val EXTRA_STAY_IMAGE : String = "extra_stay_image"
    const val EXTRA_STAY_NAME : String = "extra_stay_name"
    const val EXTRA_FROM_DATE : String = "extra_from_date"
    const val EXTRA_TO_DATE : String = "extra_to_date"
    const val EXTRA_NO_OF_GUESTS : String = "extra_no_of_guests"
    const val EXTRA_TOTAL_AMOUNT : String = "extra_total_amount"
    const val EXTRA_STAY_LOCATION : String = "extra_stay_location"
    const val EXTRA_NOTIFICATION : String = "extra_notification"

    // Storage permission
    const val READ_STORAGE_PERMISSION_CODE : Int = 2

    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE : Int = 2

    // Add Stay Activity
    const val PRODUCT_IMAGE : String = "Product_Image"

    // User Profile Image
    const val FIRST_NAME : String = "firstName"
    const val LAST_NAME : String = "lastName"
    const val IMAGE : String = "image"
    const val CONTACT_NO : String = "contactNo"
    const val USER_PROFILE_IMAGE : String = "user_profile_image"

    // Notifications
    const val ADD_STAY_SUCCESS_HEADER : String = "Stay has been uploaded!"
    const val ADD_STAY_SUCCESS_SUB_HEADER : String = "You have added the stay"
    const val BOOK_SUCCESS_HEADER : String = "Booking success!"
    const val BOOKING_SUCCESS_SUB_HEADER : String = "Congratulations! You have a trip to"
    const val CHANGE_PROFILE_SUCCESS_HEADER : String = "Profile has been updated!"
    const val CHANGE_PROFILE_SUCCESS_SUB_HEADER : String = "You have just updated your profile details"

    // Functions
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, Constants.PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
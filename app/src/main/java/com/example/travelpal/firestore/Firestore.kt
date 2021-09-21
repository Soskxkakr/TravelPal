package com.example.travelpal.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.travelpal.fragments.*
import com.example.travelpal.models.Notification
import com.example.travelpal.models.Stay
import com.example.travelpal.models.Trip
import com.example.travelpal.models.User
import com.example.travelpal.ui.*
import com.example.travelpal.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Firestore {
    // Access a Cloud Firestore instance from your Activity
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(activity : RegisterActivity, user : User) {
        db.collection(Constants.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { err ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering user",
                    err
                )
            }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        Log.i(
            "Cookies!",
            "IASDASIH"
        )
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        db.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!
                var sharedPreferences: SharedPreferences = activity.getSharedPreferences(Constants.TRAVELPAL_PREFERENCES, Context.MODE_PRIVATE)
                var editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                    is ChangeProfileDetailsActivity -> {
                        activity.getUserDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun getUserDetails(fragment: Fragment) {

        // Here we pass the collection name from which we wants the data.
        db.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(fragment.javaClass.simpleName, document.toString())
                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                when (fragment) {
                    is ProfileFragment -> {
                        // Call a function of base activity for transferring the result to it.
                        fragment.getUserDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (fragment) {
                    is ProfileFragment -> {
                        fragment.hideProgressDialog()
                    }
                }
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        db.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {
                // Notify the success result.
                when (activity) {
                    is ChangeProfileDetailsActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is ChangeProfileDetailsActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageFileURI)
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is AddStayActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is ChangeProfileDetailsActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is AddStayActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ChangeProfileDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadStayDetails(activity: AddStayActivity, stayInfo: Stay) {

        db.collection(Constants.STAYS)
            .document()
            .set(stayInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.stayUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getListOfStays(fragment : ExploreFragment, view : View) {
        db.collection(Constants.STAYS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val listOfStays: ArrayList<Stay> = ArrayList()

                for (i in document.documents) {
                    val stay = i.toObject(Stay::class.java)!!
                    stay.stayId = i.id
                    listOfStays.add(stay)
                }

                fragment.successListOfStays(listOfStays, view)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the list of stays.", e)
            }
    }

    fun getStayDetails(activity: Activity, stayId : String) {
        db.collection(Constants.STAYS)
            .document(stayId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the stay details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val stay = document.toObject(Stay::class.java)!!
                when (activity) {
                    is StayDetailsActivity -> {
                        activity.stayDetailsSuccess(stay)
                    }
                    is BookingActivity -> {
                        activity.stayDetailsSuccess(stay)
                    }
                    is TripDetailsActivity -> {
                        activity.getStayDetailsSuccess(stay)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is StayDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                    is BookingActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addToWishlists(activity : StayDetailsActivity, userId : String, stayId : String) {
        db.collection(Constants.USERS)
            .document(userId)
            .update("wishLists", FieldValue.arrayUnion(stayId))
            .addOnSuccessListener { document ->
                activity.addToWishlistsSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the wishlists.",
                    e
                )
            }
    }

    fun removeFromWishlists(activity : StayDetailsActivity, userId : String, stayId : String) {
        db.collection(Constants.USERS)
            .document(userId)
            .update("wishLists", FieldValue.arrayRemove(stayId))
            .addOnSuccessListener { document ->
                activity.removeFromWishlistsSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while removing the wishlists.",
                    e
                )
            }
    }

    fun checkWishlist(activity : StayDetailsActivity, stayId : String, userId : String)  {
        db.collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                var lists : List<String> = document.get("wishLists") as List<String>
                if (stayId in lists) {
                    activity.checkWishListComplete(true)
                } else {
                    activity.checkWishListComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    "Check Wishlist Error",
                    "Error while checking for wishlists.",
                    e
                )
            }
    }

    fun getWishlistsFromUsers(fragment : WishlistFragment, userId : String, view : View) {
        db.collection(Constants.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                var listOfStayId = document.get("wishLists") as ArrayList<String>
                fragment.getWishListsFromUsersSuccess(listOfStayId, view)
            }
            .addOnFailureListener{ e ->
                Log.e(
                    "Get Wishlist Error",
                    "Error while getting the wishlists",
                    e
                )
            }
    }

    fun getListOfStaysFromDocument(fragment : WishlistFragment, listOfStayId : ArrayList<String>, view : View) {
        db.collection(Constants.STAYS).addSnapshotListener {
            snapshot, e ->
            if (e != null) {
                Log.e(
                    "Failed listening",
                    "Listen Failed",
                    e
                )
            }
            if (snapshot != null) {
                val documents = snapshot.documents
                var listOfStays : ArrayList<Stay> = ArrayList()
                documents.forEach {
                    var stayId = it.id
                    if (stayId in listOfStayId) {
                        val stay = it.toObject(Stay::class.java)!!
                        stay.stayId = it.id
                        listOfStays.add(stay)
                    }
                }
                fragment.getListOfStaysFromDocumentSuccess(listOfStays, view)
            }
        }
    }

    fun uploadTripDetails(activity : CheckoutActivity, trip : Trip) {
        db.collection(Constants.TRIPS)
            .document()
            .set(trip, SetOptions.merge())
            .addOnSuccessListener { document ->
                activity.uploadTripSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the trip details.",
                    e
                )
            }
    }

    fun getListOfTrips(fragment : TripsFragment, view : View, userId : String) {
        db.collection(Constants.TRIPS)
            .get()
            .addOnSuccessListener { document ->
                var listOfTrips : ArrayList<Trip> = ArrayList()
                for (i in document.documents) {
                    val trip = i.toObject(Trip::class.java)!!
                    if (trip.userId == userId) {
                        listOfTrips.add(trip)
                    }
                }
                fragment.getListOfTripsSuccess(listOfTrips, view)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of trips.",
                    e
                )
            }
    }

    fun getTripDetails(activity : TripDetailsActivity, stayId : String) {
        db.collection(Constants.TRIPS)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->
                var trip : Trip = Trip()
                // Convert the snapshot to the object of Product data model class.
                for (i in document.documents) {
                    val tempTrip = i.toObject(Trip::class.java)!!
                    if (tempTrip.stayId == stayId) {
                        trip = tempTrip
                    }
                }
                activity.getTripDetailsSuccess(trip)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the trip details.", e)
            }
    }

    fun createNotification(activity : Activity, notification : Notification) {
        db.collection(Constants.NOTIFICATION)
            .document()
            .set(notification, SetOptions.merge())
            .addOnSuccessListener { document ->
                when (activity) {
                    is CheckoutActivity -> {
                        activity.createNotificationSuccess()
                    }
                    is AddStayActivity -> {
                        activity.createNotificationSuccess()
                    }
                    is ChangeProfileDetailsActivity -> {
                        activity.createNotificationSuccess()
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddStayActivity -> {
                        activity.hideProgressDialog()
                    }
                    is ChangeProfileDetailsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the trip details.",
                    e
                )
            }
    }

    fun getListOfNotifications(fragment : NotificationFragment, view : View, userId : String) {
        db.collection(Constants.NOTIFICATION)
            .get()
            .addOnSuccessListener { document ->
                var listOfNotification : ArrayList<Notification> = ArrayList()
                for (i in document.documents) {
                    val notification = i.toObject(Notification::class.java)!!
                    if (notification.userId == userId) {
                        listOfNotification.add(notification)
                    }
                }
                fragment.getListOfNotificationsSuccess(listOfNotification, view)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting the list of notifications.",
                    e
                )
            }
    }
}
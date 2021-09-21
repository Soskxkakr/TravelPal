package com.example.travelpal.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btn_sign_in).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_forgot_password).setOnClickListener(this)
        findViewById<TextView>(R.id.tv_sign_up).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.btn_sign_in -> {
                    logInRegisteredUser()
                }

                R.id.tv_forgot_password -> {
                    startActivity(
                        Intent(this, ForgotPasswordActivity::class.java)
                    )
                }

                R.id.tv_sign_up -> {
                    startActivity(
                        Intent(this, RegisterActivity::class.java)
                    )
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                }
            }
        }
    }

    private fun validateDetails() : Boolean {
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_email).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_password).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim { it <= ' ' }
            val password = findViewById<EditText>(R.id.et_password).text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Firestore().getUserDetails(this)
                    } else {
                        // Hide the progress dialog
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user : User) {
        hideProgressDialog()
        startActivity(
            Intent(this@LoginActivity, MainActivity::class.java)
        )
    }
}
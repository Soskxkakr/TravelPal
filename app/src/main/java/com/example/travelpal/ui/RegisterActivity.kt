package com.example.travelpal.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.travelpal.R
import com.example.travelpal.firestore.Firestore
import com.example.travelpal.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {
            registerUser()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }

    private fun validateRegisterDetails(): Boolean {
        var et_first_name = findViewById<EditText>(R.id.et_first_name)
        var et_last_name = findViewById<EditText>(R.id.et_last_name)
        var et_email = findViewById<EditText>(R.id.et_email)
        var et_contact_no = findViewById<EditText>(R.id.et_contact_no)
        var et_password = findViewById<EditText>(R.id.et_password)
        var et_confirm_password = findViewById<EditText>(R.id.et_confirm_password)
        var cb_tnc = findViewById<CheckBox>(R.id.cb_tnc)

        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_contact_no.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_contact), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !cb_tnc.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val firstName = findViewById<EditText>(R.id.et_first_name).text.toString().trim { it <= ' ' }
            val lastName = findViewById<EditText>(R.id.et_last_name).text.toString().trim { it <= ' ' }
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim { it <= ' ' }
            val contactNo = findViewById<EditText>(R.id.et_contact_no).text.toString().trim { it <= ' ' }
            val password = findViewById<EditText>(R.id.et_password).text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(
                            firebaseUser.uid,
                            firstName,
                            lastName,
                            email,
                            contactNo
                        )
                        Firestore().registerUser(this, user)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }
}